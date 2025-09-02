package com.heejae.tenniverse.persentation.login

import android.view.inputmethod.InputMethodManager
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentCareerBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.util.append8
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CareerFragment : BaseFragment<FragmentCareerBinding>(R.layout.fragment_career) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun initView() {
        initBinding()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        setFocusing(binding.etCareer, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setListener() {
        binding.btnNext.setOnClickListener {
            (requireActivity() as LoginActivity).nextPager(4)
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel

        binding.tvDesCheckUser.text = buildSpannedString {
            append8(requireActivity(), text = "팀 밸런스 조절을 위해 ")
            append8(requireActivity(), color = R.color.green, text = "당신의 테니스 구력")
            append8(requireActivity(), text = "을 알려 주세요!")
        }
    }

    companion object {
        fun newInstance() = CareerFragment()
    }
}