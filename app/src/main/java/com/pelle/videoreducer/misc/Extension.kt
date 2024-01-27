package com.pelle.videoreducer.misc

import android.R.attr
import android.app.Activity
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import com.pelle.videoreducer.data.VideoModel
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

private const val ONE_MB_IN_BYTES = 1000000

fun File.getThumbnail(activity: Activity?) : Bitmap? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        var bitmap: Bitmap? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(activity, Uri.parse(absolutePath))
            bitmap = mediaMetadataRetriever.getFrameAtTime(
                1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        bitmap
    } else {
         ThumbnailUtils.createVideoThumbnail(absolutePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
}

fun File.toVideoModel(activity: Activity? = null) = VideoModel(
    fileName = nameWithoutExtension,
    absolutePath = absolutePath,
    fileSize = getFileSizeInMb(),
    thumbnail = getThumbnail(activity),
)

fun File.getFileSizeInMb() =
    BigDecimal(length() / ONE_MB_IN_BYTES).setScale(2, RoundingMode.FLOOR).toDouble()