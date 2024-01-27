package com.pelle.videoreducer.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pelle.videoreducer.R
import com.pelle.videoreducer.data.UserPreferences

class OnBoardingViewModel(
    private val userPreferences: UserPreferences,
): ViewModel() {
    // region PROPERTIES
    private var hasReachedLastStep = false
    // endregion

    // region LIVEDATA
    private val onBoardingFinished = MutableLiveData(userPreferences.hasFinishedOnBoarding)
    val onBoardingFinishedLiveData = onBoardingFinished as LiveData<Boolean>

    private val goToNextStep = MutableLiveData(false)
    val goToNextStepLiveData = goToNextStep as LiveData<Boolean>

    private val continueButtonTextId = MutableLiveData(R.string.continue_button)
    val continueButtonTextIdLiveData = continueButtonTextId as LiveData<Int>
    // endregion

    // region PUBLIC METHODS
    fun onStepChanged(step: Int) {
        continueButtonTextId.value = if (step == fragments.size - 1) {
            if (hasReachedLastStep) {
                finishOnBoarding()
            } else {
                hasReachedLastStep = true
            }
            R.string.start_button
        } else {
            hasReachedLastStep = false
            R.string.continue_button
        }
    }

    fun changeStep(step: Int) {
        goToNextStep.value = true
        onStepChanged(step)
    }
    // endregion

    // region PRIVATE METHODS
    private fun finishOnBoarding() {
        userPreferences.hasFinishedOnBoarding = true
        onBoardingFinished.value = true
    }
    // endregion
}