package com.example.kotlin_admob_test_1.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardingViewModel : ViewModel() {

    private val _navigateToNext = MutableLiveData<Event<Unit>>()
    val navigateToNext: LiveData<Event<Unit>> = _navigateToNext
    var hasShownPage1NativeAd = false

    fun onNextClicked() {
        _navigateToNext.postValue(Event(Unit))
    }

    override fun onCleared() {
        super.onCleared()
    }
}

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}
