package com.example.kotlin_admob_test_1

import android.content.Intent
import android.os.Bundle
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

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> Fragment()
                    1 -> OnboardingPage1Fragment()
                    2 -> NativeAdFullFragment()
                    3 -> OnboardingPage2Fragment()
                    else -> OnboardingPage3Fragment()
                }
            }
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(currentPage, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                    finish()
                } else if (currentPage == 3 && position == 2) {
                    binding.viewPager.post {
                        binding.viewPager.setCurrentItem(1, false)
                    }
                }

                currentPage = position

                binding.viewPager.isUserInputEnabled = position != 2
            }
        })

        viewModel.loadNativeAd(this)
        viewModel.navigateToPage2.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.viewPager.setCurrentItem(3, true)
            }
        }

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
