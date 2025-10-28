package com.example.kotlin_admob_test_1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.kotlin_admob_test_1.databinding.ActivityOnboardingBinding
import com.example.kotlin_admob_test_1.ui.onboarding.NativeAdFullFragment
import com.example.kotlin_admob_test_1.ui.onboarding.OnboardingPage1Fragment
import com.example.kotlin_admob_test_1.ui.onboarding.OnboardingPage2Fragment
import com.example.kotlin_admob_test_1.ui.onboarding.OnboardingPage3Fragment
import com.example.kotlin_admob_test_1.ui.onboarding.OnboardingViewModel
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private var currentPage = 1
    private var nativeAdFullFragment: NativeAdFullFragment? = null

    private class FadeOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.translationX = -page.width * position
            page.alpha = 1 - abs(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> Fragment() // For skipping
                    1 -> OnboardingPage1Fragment()
                    2 -> {
                        if (nativeAdFullFragment == null) {
                            nativeAdFullFragment = NativeAdFullFragment().apply {
                                onAdClosedListener = {
                                    binding.viewPager.setCurrentItem(3, true)
                                }
                            }
                        }
                        nativeAdFullFragment!!
                    }
                    3 -> OnboardingPage2Fragment()
                    else -> OnboardingPage3Fragment()
                }
            }
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(currentPage, false)
        binding.viewPager.setPageTransformer(FadeOutPageTransformer())

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val previousPage = currentPage
                currentPage = position

                if (position == 0) { // Swiped to exit
                    startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                    finish()
                    return
                }

                // Disable swiping on the ad page
                binding.viewPager.isUserInputEnabled = (position != 2)

                // When swiping backward from Page 2 to the Ad page, skip the ad
                if (previousPage == 3 && position == 2) {
                    binding.viewPager.post { binding.viewPager.setCurrentItem(1, false) }
                }
            }
        })

        viewModel.navigateToNext.observe(this) {
            it.getContentIfNotHandled()?.let {
                val currentItem = binding.viewPager.currentItem
                if (currentItem < (binding.viewPager.adapter?.itemCount ?: 0) - 1) {
                    binding.viewPager.setCurrentItem(currentItem + 1, true)
                }
            }
        }
    }
}
