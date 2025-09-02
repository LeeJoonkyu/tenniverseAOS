package com.heejae.tenniverse.persentation.login

import android.util.Log
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentLoginBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.persentation.main.adapter.LoginPagerAdapter

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val fragments = listOf(
        IdentificationFragment.newInstance(),
        NameFragment.newInstance(),
        GenderFragment.newInstance(),
        CareerFragment.newInstance(),
        ProfileFragment.newInstance(),
    )

    override fun initView() {
        Log.d(this@LoginFragment.javaClass.name, "fragments: ${parentFragmentManager.fragments}")
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.adapter = LoginPagerAdapter(requireActivity(), fragments)
    }

    fun nextPager(position: Int) {
        binding.viewpager.currentItem = position
    }
}