package com.heejae.tenniverse.persentation.login

import android.view.inputmethod.InputMethodManager
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentNameBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.util.append8

class NameFragment: BaseFragment<FragmentNameBinding>(R.layout.fragment_name) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun initView() {
        initBinding()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        setFocusing(binding.etName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setListener() {
        binding.btnNext.setOnClickListener {
            (requireActivity() as LoginActivity).nextPager(2)
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel

        binding.tvDesCheckUser.text = buildSpannedString {
            append8(requireActivity(), text = "프로필 생성을 위해\n")
            append8(requireActivity(), color = R.color.green, text = "당신의 성함")
            append8(requireActivity(), text = "을 알려 주세요!")
        }
    }
    companion object {
        fun newInstance() = NameFragment()
    }
}