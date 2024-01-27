package com.pelle.videoreducer.ui.compressvideo

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pelle.videoreducer.data.VideoModel
import com.pelle.videoreducer.misc.toVideoModel
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

private const val COMPRESSED_VIDEOS_FOLDER_NAME = "compressed-videos"
private const val EVENT_VIDEO_COMPRESS_SUCCESS = "video_compress_success"
private const val EVENT_VIDEO_COMPRESS_ERROR = "video_compress_error"

class CompressVideoViewModel(
    private val firebaseAnalytics: FirebaseAnalytics,
): ViewModel() {

    // region PROPERTIES
    private var selectedQuality = VideoQuality.MEDIUM
    private lateinit var selectedVideo: VideoModel
    private var videoCancelled = false
    // endregion

    // region LIVEDATA
    private val _viewState = MutableLiveData<CompressVideoViewState>()
    val viewState: LiveData<CompressVideoViewState>
        get() = _viewState
    // endregion

    // region PUBLIC METHODS
    fun onViewEvent(viewEvent: CompressVideoViewEvent) {
        when (viewEvent) {
            is CompressVideoViewEvent.InitViewModel -> {
                selectedVideo = viewEvent.video
                sendVideoSizes()
            }
            is CompressVideoViewEvent.CompressVideo -> compressVideo(viewEvent.context)
            is CompressVideoViewEvent.ChangeQuality -> changeVideoQuality(viewEvent.videoQuality)
            CompressVideoViewEvent.CancelCompression -> cancelCompression()
        }
    }
    // endregion

    // region PRIVATE METHODS
    private fun sendVideoSizes() {
        _viewState.value = CompressVideoViewState.SetUpVideoSizes(
            currentSize = selectedVideo.fileSize,
            estimatedSize = selectedVideo.getEstimatedSize(selectedQuality),
            selectedQuality = selectedQuality,
        )
    }

    private fun compressVideo(context: Context) {
        VideoCompressor.start(
            context = context,
            uris = listOf(File(selectedVideo.absolutePath).toUri()),
            isStreamable = false,
            sharedStorageConfiguration = SharedStorageConfiguration(
                saveAt = SaveLocation.movies,
                subFolderName = COMPRESSED_VIDEOS_FOLDER_NAME
            ),
            configureWith = Configuration(
                quality = selectedQuality,
                isMinBitrateCheckEnabled = false,
                videoBitrateInMbps = null,
                disableAudio = false,
                keepOriginalResolution = true,
                videoWidth = null,
                videoHeight = null,
                videoNames = listOf(selectedVideo.fileName)
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    if (!videoCancelled) {
                        _viewState.postValue(CompressVideoViewState.SetUpProgress(percent.toInt()))
                    }
                }

                override fun onStart(index: Int) {
                    videoCancelled = false
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    firebaseAnalytics.logEvent(
                        EVENT_VIDEO_COMPRESS_SUCCESS,
                        bundleOf(EVENT_VIDEO_COMPRESS_SUCCESS to true)
                    )
                    path?.let { safePath ->
                        val newVideo = File(safePath).toVideoModel()
                        _viewState.postValue(
                            CompressVideoViewState.Completed(
                                safePath,
                                BigDecimal(selectedVideo.fileSize - newVideo.fileSize).setScale(2, RoundingMode.CEILING).toDouble()
                            ),
                        )
                        cancelCompression()
                    } ?: _viewState.postValue(CompressVideoViewState.Error)
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    firebaseAnalytics.logEvent(
                        EVENT_VIDEO_COMPRESS_ERROR,
                        bundleOf(EVENT_VIDEO_COMPRESS_ERROR to true)
                    )
                    FirebaseCrashlytics.getInstance().recordException(Exception(failureMessage))
                    _viewState.postValue(CompressVideoViewState.Error)
                }

                override fun onCancelled(index: Int) { /* no-op */ }
            }
        )
    }

    private fun changeVideoQuality(videoQuality: VideoQuality) {
        selectedQuality = videoQuality
        sendVideoSizes()
    }

    private fun cancelCompression() {
        videoCancelled = true
        VideoCompressor.cancel()
    }
    // endregion
}

sealed class CompressVideoViewState {
    data class SetUpVideoSizes(
        val currentSize: Double,
        val estimatedSize: Double,
        val selectedQuality: VideoQuality,
    ) : CompressVideoViewState()
    data class SetUpProgress(val progress: Int) : CompressVideoViewState()
    data class Completed(
        val videoPath: String,
        val reducedMb: Double,
    ) : CompressVideoViewState()
    data object Error : CompressVideoViewState()
}

sealed class CompressVideoViewEvent {
    data class InitViewModel(val video: VideoModel) : CompressVideoViewEvent()
    data class CompressVideo(val context: Context) : CompressVideoViewEvent()
    data class ChangeQuality(val videoQuality: VideoQuality) : CompressVideoViewEvent()
    data object CancelCompression : CompressVideoViewEvent()
}