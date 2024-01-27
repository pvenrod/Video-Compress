package com.pelle.videoreducer.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pelle.videoreducer.databinding.FragmentFirstStepOnboardingBinding

class FirstStepOnBoardingFragment: Fragment() {

    private lateinit var binding: FragmentFirstStepOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstStepOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

}