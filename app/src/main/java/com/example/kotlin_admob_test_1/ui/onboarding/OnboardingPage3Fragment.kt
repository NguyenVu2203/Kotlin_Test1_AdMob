package com.example.kotlin_admob_test_1.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kotlin_admob_test_1.R
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class OnboardingPage3Fragment : Fragment() {

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_page_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adFrame = view.findViewById<FrameLayout>(R.id.native_ad_frame)
        adFrame.visibility = View.GONE // Hide by default

        view.findViewById<TextView>(R.id.tvNext).setOnClickListener {
            viewModel.onNextClicked()
        }

        viewModel.nativeAd.observe(viewLifecycleOwner) { ad ->
            if (ad != null) {
                populateNativeAdView(ad, adFrame)
                adFrame.visibility = View.VISIBLE
            } else {
                adFrame.visibility = View.GONE
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adFrame: FrameLayout) {
        val adView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.adChoicesView = adView.findViewById(R.id.ad_choices_container)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent

        val closeBtn = adView.findViewById<ImageButton>(R.id.btn_close_ad)

        closeBtn.setOnClickListener {
            adFrame.removeAllViews()
            adFrame.visibility = View.GONE
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

        adFrame.removeAllViews()
        adFrame.addView(adView)
    }
}
