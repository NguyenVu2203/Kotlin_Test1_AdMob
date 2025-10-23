package com.example.kotlin_admob_test_1.ads

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kotlin_admob_test_1.MainActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialManager(private val activity: Activity) {

    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "InterstitialManager"

    fun loadAndShowInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        val adUnitId = "ca-app-pub-3940256099942544/1033173712"

        InterstitialAd.load(
            activity,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "onAdLoaded")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "onAdDismissedFullScreenContent")
                            goToMain()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                            goToMain()
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "onAdShowedFullScreenContent")
                            mInterstitialAd = null
                        }
                    }
                    showInterstitialAd()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "onAdFailedToLoad: ${loadAdError.message}")
                    goToMain()
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad was not ready to be shown.")
            goToMain()
        }
    }

    private fun goToMain() {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }
}
