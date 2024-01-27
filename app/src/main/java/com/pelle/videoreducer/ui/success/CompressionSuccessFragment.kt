package com.pelle.videoreducer.ui.success

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.play.core.review.ReviewManagerFactory
import com.pelle.videoreducer.R
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.databinding.FragmentCompressionSuccessBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val MIME_TYPE_MP4 = "video/*"

class CompressionSuccessFragment: Fragment() {

    // region PROPERTIES
    private lateinit var binding: FragmentCompressionSuccessBinding
    private val args: CompressionSuccessFragmentArgs by navArgs()
    private val viewModel by viewModel<CompressionSuccessViewModel>()
    private val adManager: AdManager by inject()
    // endregion

    // region OBSERVERS
    private val compressVideoObserver = Observer<CompressionSuccessViewState> { viewState ->
        when (viewState) {
            else -> {}
        }
    }
    // endregion

    // region OVERRIDDEN METHODS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompressionSuccessBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpViews()
        requestReview()
        adManager.showBanner(binding.bannerAd)
    }
    // endregion

    // region PRIVATE METHODS
    private fun setUpObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, compressVideoObserver)
    }

    private fun setUpViews() {
        with (binding) {
            descriptionText.text = getString(R.string.success_compression_desc, args.reducedMb)
            videoThumb.setImageBitmap(args.thumbnail)
            previewBtn.setOnClickListener {
                adManager.showInterstitial(requireActivity()) {
                    playVideo()
                }
            }
            shareBtn.setOnClickListener {
                adManager.showInterstitial(requireActivity()) {
                    shareVideo()
                }
            }
            compressAnotherBtn.setOnClickListener {
                adManager.showInterstitial(requireActivity()) {
                    compressAnotherVideo()
                }
            }
        }
    }

    private fun playVideo() {
        try {
            val videoUri = Uri.parse(args.video.absolutePath)
            val intent = Intent(Intent.ACTION_VIEW, videoUri)
            intent.setDataAndType(videoUri, MIME_TYPE_MP4)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, R.string.cant_play_video, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareVideo() {
        ShareCompat.IntentBuilder.from(requireActivity())
            .setStream(Uri.parse(args.video.absolutePath))
            .setType(MIME_TYPE_MP4)
            .setChooserTitle(getString(R.string.share_message))
            .startChooser()
    }

    private fun compressAnotherVideo() {
        findNavController().popBackStack(R.id.selectVideoFragment, true)
    }

    private fun requestReview() {
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(requireActivity(), reviewInfo)
            }
        }
    }
    // endregion
}