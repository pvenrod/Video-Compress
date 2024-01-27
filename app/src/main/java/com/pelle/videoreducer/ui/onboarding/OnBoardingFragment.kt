package com.pelle.videoreducer.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pelle.videoreducer.ads.AdManager
import com.pelle.videoreducer.databinding.FragmentOnboardingBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingFragment: Fragment() {
    // region PROPERTIES
    private lateinit var binding: FragmentOnboardingBinding
    private val viewModel by viewModel<OnBoardingViewModel>()
    // endregion

    // region OBSERVERS
    private var finishedOnBoardingObserver = Observer<Boolean> { isFinished ->
        if (isFinished) {
            findNavController().navigate(OnBoardingFragmentDirections.goToSelectVideoFragment())
        }
    }
    private var continueButtonTextIdObserver = Observer<Int> { buttonTextId ->
        binding.continueButton.text = getString(buttonTextId)
    }
    private var goToNextStepObserver = Observer<Boolean> { changed ->
        if (changed) {
            binding.stepViewPager.setCurrentItem(binding.stepViewPager.currentItem + 1, true)
        }
    }
    // endregion

    // region OVERRIDDEN METHODS
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setUpViews()
    }
    // endregion

    // region PRIVATE METHODS
    private fun setUpObservers() {
        with (viewModel) {
            onBoardingFinishedLiveData.observe(viewLifecycleOwner, finishedOnBoardingObserver)
            continueButtonTextIdLiveData.observe(viewLifecycleOwner, continueButtonTextIdObserver)
            goToNextStepLiveData.observe(viewLifecycleOwner, goToNextStepObserver)
        }
    }

    private fun setUpViews() {
        with (binding) {
            stepViewPager.adapter = OnBoardingAdapter(requireActivity())
            TabLayoutMediator(viewPagerTabLayout, stepViewPager) { _, _ ->
                // no-op
            }.attach()
            stepViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    viewModel.onStepChanged(position)
                }
            })
            continueButton.setOnClickListener {
                viewModel.changeStep(stepViewPager.currentItem)
            }
        }
    }
    // endregion
}