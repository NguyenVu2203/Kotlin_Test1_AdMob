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

    // Class để thay đổi hiệu ứng lướt trang thành mờ dần (fade)
    private class FadeOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            // Chống lại hiệu ứng trượt mặc định
            page.translationX = -page.width * position
            // Làm mờ trang khi nó di chuyển ra khỏi trung tâm
            page.alpha = 1 - abs(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Order: Skip | Page 1 | Ad | Page 2 | Page 3
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> Fragment() // For skipping to MainActivity
                    1 -> OnboardingPage1Fragment()
                    2 -> {
                        if (nativeAdFullFragment == null) {
                            nativeAdFullFragment = NativeAdFullFragment().apply {
                                onAdClosedListener = {
                                    // When ad is closed, go to Page 2
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

        // Đặt hiệu ứng chuyển trang tùy chỉnh
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

                // --- This is the core logic ---
                if (previousPage == 1 && position == 2) {
                    // SWIPING FORWARD: From Page 1 to Ad page
                    binding.viewPager.isUserInputEnabled = false // Disable swipe on Ad
                    nativeAdFullFragment?.loadAd(this@OnboardingActivity)
                } else if (previousPage == 3 && position == 2) {
                    // SWIPING BACKWARD: From Page 2, trying to go to Ad page
                    // Skip the ad and go directly to Page 1
                    binding.viewPager.post { binding.viewPager.setCurrentItem(1, false) }
                } else {
                    // All other pages have swiping enabled
                    binding.viewPager.isUserInputEnabled = true
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
