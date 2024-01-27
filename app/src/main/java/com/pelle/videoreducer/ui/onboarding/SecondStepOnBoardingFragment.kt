package com.pelle.videoreducer.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pelle.videoreducer.databinding.FragmentSecondStepOnboardingBinding

class SecondStepOnBoardingFragment: Fragment() {

    private lateinit var binding: FragmentSecondStepOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecondStepOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

}