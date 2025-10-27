package com.example.kotlin_admob_test_1.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin_admob_test_1.R
import com.example.kotlin_admob_test_1.ads.InterstitialManager
import com.google.android.gms.ads.MobileAds

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private var progressStatus = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progressBar)
        progressBar.progress = 0

        MobileAds.initialize(this) { initializationStatus ->
            Log.d(TAG, "Mobile Ads SDK initialized.")
            simulateLoading()
        }
    }

    private fun simulateLoading() {
        runnable = Runnable {
            if (progressStatus < 100) {
                progressStatus += 2
                progressBar.progress = progressStatus
                handler.postDelayed(runnable, 100)
            } else {
                val interstitialManager = InterstitialManager(this@SplashActivity)
                interstitialManager.loadAndShowInterstitialAd()
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}