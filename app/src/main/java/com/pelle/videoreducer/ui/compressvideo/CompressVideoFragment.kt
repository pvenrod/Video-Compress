package com.pelle.videoreducer.ui.compressvideo

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.google.android.material.button.MaterialButton
import com.pelle.videoreducer.R
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.data.VideoModel
import com.pelle.videoreducer.databinding.FragmentCompressVideoBinding
import com.pelle.videoreducer.misc.toVideoModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class CompressVideoFragment: Fragment() {

    // region PROPERTIES
    private lateinit var binding: FragmentCompressVideoBinding
    private val args: CompressVideoFragmentArgs by navArgs()
    private val viewModel by viewModel<CompressVideoViewModel>()
    private val adManager: AdManager by inject()
    private var isCompressing = false
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isCompressing) {
                showCancelDialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }
    // endregion

    // region OBSERVERS
    private val compressVideoObserver = Observer<CompressVideoViewState> { viewState ->
        when (viewState) {
            is CompressVideoViewState.SetUpVideoSizes -> setUpVideoSizes(
                viewState.currentSize,
                viewState.estimatedSize,
                viewState.selectedQuality,
            )
            is CompressVideoViewState.SetUpProgress -> setUpProgress(viewState.progress)
            CompressVideoViewState.Error -> showError()
            is CompressVideoViewState.Completed -> goToSuccessScreen(viewState.videoPath, viewState.reducedMb)
        }
    }
    // endregion

    // region OVERRIDDEN METHODS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompressVideoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpViews()
        setUpListeners()
        adManager.showBanner(binding.bannerAd)
        viewModel.onViewEvent(CompressVideoViewEvent.InitViewModel(args.video))
    }
    // endregion

    // region PRIVATE METHODS
    private fun setUpObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, compressVideoObserver)
    }

    private fun setUpViews() {
        with (binding) {
            videoThumb.setImageBitmap(args.video.thumbnail)
            with (cancelCompressDialog) {
                cancelBackPressButton.setOnClickListener {
                    hideCancelDialog()
                }
                confirmBackPressButton.setOnClickListener {
                    confirmBackPress()
                    Handler(Looper.getMainLooper()).postDelayed({
                        confirmBackPress()
                    }, 200)
                }
            }
        }
    }

    private fun setUpVideoSizes(
        currentSize: Double,
        estimatedSize: Double,
        selectedQuality: VideoQuality,
    ) {
        with (binding) {
            currentSizeText.text = getString(R.string.current_size, currentSize.toString())
            estimatedSizeText.text = getString(
                R.string.estimated_size,
                estimatedSize.toString(),
            )
            listOf(lowQualityButton, mediumQualityButton, highQualityButton).forEach { btn ->
                btn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.qualityNotSelected))
            }
            when (selectedQuality) {
                VideoQuality.LOW -> lowQualityButton
                VideoQuality.MEDIUM -> mediumQualityButton
                VideoQuality.HIGH -> highQualityButton
                else -> mediumQualityButton
            }.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.qualitySelected))
        }
    }

    private fun setUpProgress(progress: Int) {
        startCompressing()
        with (binding) {
            compressVideoButton.apply {
                alpha = 0.8f
                text = getString(
                    R.string.compression_progress,
                    progress.toString(),
                )
                isEnabled = false
            }

            ConstraintSet().apply {
                clone(compressVideoProgressLayout)
                constrainPercentWidth(R.id.compressVideoProgress, progress.toFloat() / 100f)
                applyTo(compressVideoProgressLayout)
            }
        }
    }

    private fun setUpListeners() {
        with (binding) {
            listOf(
                highQualityButton to VideoQuality.HIGH,
                mediumQualityButton to VideoQuality.MEDIUM,
                lowQualityButton to VideoQuality.LOW
            ).forEach { viewAndQuality ->
                viewAndQuality.first.setOnClickListener {
                    if (!isCompressing) {
                        viewModel.onViewEvent(
                            CompressVideoViewEvent.ChangeQuality(
                                viewAndQuality.second
                            )
                        )
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(callback)
            compressVideoButton.setOnClickListener {
                it.apply {
                    isEnabled = false
                    alpha = 0.8f
                    (this as MaterialButton).text = getString(R.string.loading)
                }
                viewModel.onViewEvent(CompressVideoViewEvent.CompressVideo(requireContext()))
            }
        }
    }

    private fun confirmBackPress() {
        viewModel.onViewEvent(CompressVideoViewEvent.CancelCompression)
        setUpProgress(0)
        stopCompressing()
        with (binding) {
            compressVideoButton.apply {
                alpha = 1f
                text = getString(R.string.compress_video_button)
                isEnabled = true
            }
        }
        hideCancelDialog()
    }

    private fun showCancelDialog() {
        binding.cancelCompressDialog.root.isVisible = true
    }

    private fun hideCancelDialog() {
        binding.cancelCompressDialog.root.isVisible = false
    }

    private fun showError() {
        Toast.makeText(context, R.string.error_while_compressing, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun goToSuccessScreen(
        videoPath: String,
        reducedMb: Double,
    ) {
        adManager.showInterstitial(requireActivity()) {
            stopCompressing()
            findNavController().navigate(
                CompressVideoFragmentDirections.goToCompressionSuccessFragment(
                    File(videoPath).toVideoModel(activity),
                    reducedMb.toString(),
                    args.video.thumbnail,
                ),
            )
        }
    }

    private fun startCompressing() {
        isCompressing = true
        callback.isEnabled = true
        binding.descriptionText.text = getString(R.string.dont_leave_app)
    }

    private fun stopCompressing() {
        isCompressing = false
        callback.isEnabled = false
        binding.descriptionText.text = getString(R.string.select_quality_desc)
    }
    // endregion
}