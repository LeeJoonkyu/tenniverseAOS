package com.heejae.tenniverse.persentation.login

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentLoginBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.persentation.main.adapter.LoginPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: BaseActivity<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: MainViewModel by viewModels()

    private val fragments = listOf(
        IdentificationFragment.newInstance(),
        NameFragment.newInstance(),
        GenderFragment.newInstance(),
        CareerFragment.newInstance(),
        ProfileFragment.newInstance(),
    )

    override fun setViewModel() = viewModel

    override fun initView() {
        initBinding()
        setViewPager()
        setListener()
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun setListener() {
        binding.ivBack.setOnClickListener {
            backPager()
        }
    }
    private fun setViewPager() {

        binding.viewpager.apply {
            isUserInputEnabled = false
            adapter = LoginPagerAdapter(this@LoginActivity, fragments)
            offscreenPageLimit = 5

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.tvSkip.isVisible = position == 4
                }
            })
        }

        binding.dotsIndicator.attachTo(binding.viewpager)
    }

    fun backPager() {
        val position = binding.viewpager.currentItem
        if (position == 0) {
            finish()
        }else {
            binding.viewpager.currentItem = position - 1
        }
    }

    fun nextPager(position: Int) {
        binding.viewpager.currentItem = position
    }
}