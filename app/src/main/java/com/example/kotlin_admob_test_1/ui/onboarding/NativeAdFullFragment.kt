package com.example.kotlin_admob_test_1.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kotlin_admob_test_1.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class NativeAdFullFragment : Fragment() {

    private var nativeAd: NativeAd? = null
    private lateinit var adView: NativeAdView
    var onAdClosedListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_native_ad_full, container, false)
        adView = view.findViewById(R.id.native_ad_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // If the ad is already loaded, populate the view.
        nativeAd?.let {
            populateNativeAdView(it, adView)
        }
    }

    fun loadAd(context: Context) {
        val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110") // Test ID
            .forNativeAd { ad: NativeAd ->
                // Ad successfully loaded.
                this.nativeAd = ad
                if (isAdded) {
                    populateNativeAdView(ad, adView)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("NativeAdFullFragment", "Ad failed to load: ${adError.message}")
                    onAdClosedListener?.invoke()
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        val hasVideo = nativeAd.mediaContent?.hasVideoContent() == true
        Log.d("NativeAdCheck", "Ad received. Does it have video content? $hasVideo")

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.mediaView = adView.findViewById(R.id.ad_media)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        val closeBtn = adView.findViewById<ImageButton>(R.id.btn_close_ad)
        closeBtn.setOnClickListener {
            onAdClosedListener?.invoke()
        }

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

    override fun onDestroyView() {
        nativeAd?.destroy()
        super.onDestroyView()
    }
}
