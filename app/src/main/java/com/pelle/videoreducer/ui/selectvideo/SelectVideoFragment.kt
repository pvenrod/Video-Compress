package com.pelle.videoreducer.ui.selectvideo

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.atwa.filepicker.core.FilePicker
import com.pelle.videoreducer.App
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.data.VideoModel
import com.pelle.videoreducer.databinding.FragmentSelectVideoBinding
import org.koin.android.ext.android.inject
import java.math.BigDecimal
import java.math.RoundingMode

class SelectVideoFragment: Fragment() {

    // region PROPERTIES
    private lateinit var binding: FragmentSelectVideoBinding
    private val filePicker = FilePicker.getInstance(this)
    private val adManager: AdManager by inject()
    // endregion

    // region OVERRIDDEN METHODS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectVideoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
        adManager.showBanner(binding.bannerAd)
    }
    // endregion

    // region PRIVATE METHODS
    private fun setUpViews() {
        with (binding) {
            selectVideoButton.setOnClickListener {
                (activity?.application as? App)?.comesFromFilePicker = true
                filePicker.pickVideo { video ->
                    video?.let {
                        adManager.showInterstitial(requireActivity()) {
                            findNavController().navigate(
                                SelectVideoFragmentDirections.goToCompressVideoFragment(
                                    VideoModel(
                                        fileName = video.name ?: "",
                                        absolutePath = video.file?.absolutePath ?: "",
                                        thumbnail = video.thumbnail,
                                        fileSize = BigDecimal((video.sizeKb?.toDouble() ?: 1.0) / 1024.0).setScale(2, RoundingMode.CEILING).toDouble(),
                                    ),
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setUpPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }*/
    }
    // endregion
}