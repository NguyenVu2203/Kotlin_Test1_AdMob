package com.example.kotlin_admob_test_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class MainActivity : AppCompatActivity() {

    private var currentNativeAd: NativeAd? = null
    private val TAG = "MainActivity"
    private var selectedItem: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val imgCheck = findViewById<ImageView>(R.id.imgCheck)
        imgCheck.visibility = View.GONE

        imgCheck.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }

        val items = listOf(
            findViewById<LinearLayout>(R.id.itemHindi),
            findViewById<LinearLayout>(R.id.itemSpanish),
            findViewById<LinearLayout>(R.id.itemFrench),
            findViewById<LinearLayout>(R.id.itemEnglish),
            findViewById<LinearLayout>(R.id.itemPortuguese),
            findViewById<LinearLayout>(R.id.itemKorean),
            findViewById<LinearLayout>(R.id.itemKorean2)
        )

        items.forEach { item ->
            item.setOnClickListener {
                if (selectedItem == item) {
                    item.isSelected = false
                    selectedItem = null
                    imgCheck.visibility = View.GONE
                    return@setOnClickListener
                }
                selectedItem?.isSelected = false

                item.isSelected = true
                selectedItem = item
                imgCheck.visibility = View.VISIBLE
            }
        }

        // Xử lý edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Khởi tạo Google Ads SDK
        MobileAds.initialize(this) {
            Log.d(TAG, "Mobile Ads SDK initialized.")
            loadNativeAd()
        }
    }

    private fun loadNativeAd() {
        val adFrame = findViewById<FrameLayout>(R.id.native_ad_frame)
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                if (isDestroyed) {
                    ad.destroy()
                    return@forNativeAd
                }

                currentNativeAd?.destroy()
                currentNativeAd = ad

                val adView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
                populateNativeAdView(ad, adView)

                adFrame.removeAllViews()
                adFrame.addView(adView)
                adFrame.visibility = View.VISIBLE

                val closeBtn = adView.findViewById<ImageButton>(R.id.btn_close_ad)

                closeBtn.setOnClickListener {
                    adFrame.visibility = View.GONE
                    currentNativeAd?.destroy()
                    currentNativeAd = null
                }

                Log.d(TAG, "Native ad tải thành công")
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Native ad tải thất bại: ${adError.message}")
                    adFrame.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.mediaView = adView.findViewById(R.id.ad_media)

        val adChoicesView = adView.findViewById<com.google.android.gms.ads.nativead.AdChoicesView>(R.id.ad_choices_container)
        adView.adChoicesView = adChoicesView

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    override fun onDestroy() {
        currentNativeAd?.destroy()
        super.onDestroy()
    }
}
