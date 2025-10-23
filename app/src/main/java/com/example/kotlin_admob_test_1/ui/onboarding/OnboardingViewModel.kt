package com.example.kotlin_admob_test_1.ui.onboarding

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class OnboardingViewModel : ViewModel() {

    private val _nativeAd = MutableLiveData<NativeAd?>()
    val nativeAd: LiveData<NativeAd?> = _nativeAd

    private val _isAdLoaded = MutableLiveData<Boolean>()
    val isAdLoaded: LiveData<Boolean> = _isAdLoaded

    private val _navigateToPage2 = MutableLiveData<Event<Unit>>()
    val navigateToPage2: LiveData<Event<Unit>> = _navigateToPage2

    private val _navigateToNext = MutableLiveData<Event<Unit>>()
    val navigateToNext: LiveData<Event<Unit>> = _navigateToNext

    private var adLoader: AdLoader? = null

    fun loadNativeAd(context: Context) {
        if (adLoader != null || _nativeAd.value != null) {
            return
        }

        _isAdLoaded.postValue(false)

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                _nativeAd.postValue(ad)
                _isAdLoaded.postValue(true)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("OnboardingViewModel", "Native ad failed tải thất bại: ${adError.message}")
                    _nativeAd.postValue(null)
                    _isAdLoaded.postValue(false)
                }
            })
            .withNativeAdOptions(adOptions)
            .build()

        adLoader?.loadAd(AdRequest.Builder().build())
    }

    fun onCloseAdClicked() {
        _navigateToPage2.postValue(Event(Unit))
    }

    fun onNextClicked() {
        _navigateToNext.postValue(Event(Unit))
    }

    override fun onCleared() {
        _nativeAd.value?.destroy()
        _nativeAd.postValue(null)
        _isAdLoaded.postValue(false)
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
