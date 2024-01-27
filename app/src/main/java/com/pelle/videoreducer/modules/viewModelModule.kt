package com.pelle.videoreducer.modules

import com.pelle.videoreducer.ui.compressvideo.CompressVideoViewModel
import com.pelle.videoreducer.ui.onboarding.OnBoardingViewModel
import com.pelle.videoreducer.ui.success.CompressionSuccessViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var viewModelModule = module {
    viewModel {
        OnBoardingViewModel(
            userPreferences = get(),
        )
    }

    viewModel {
        CompressVideoViewModel(
            firebaseAnalytics = get(),
        )
    }

    viewModel {
        CompressionSuccessViewModel()
    }
}