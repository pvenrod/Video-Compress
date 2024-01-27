package com.pelle.videoreducer.ui.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompressionSuccessViewModel: ViewModel() {

    // region PROPERTIES

    // endregion

    // region LIVEDATA
    private val _viewState = MutableLiveData<CompressionSuccessViewState>()
    val viewState: LiveData<CompressionSuccessViewState>
        get() = _viewState
    // endregion

    // region PUBLIC METHODS
    fun onViewEvent(viewEvent: CompressionSuccessViewEvent) {
        when (viewEvent) {
            else -> {}
        }
    }
    // endregion

    // region PRIVATE METHODS

    // endregion
}

sealed class CompressionSuccessViewState {

}

sealed class CompressionSuccessViewEvent {

}