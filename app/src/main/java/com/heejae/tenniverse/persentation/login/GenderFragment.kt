package com.heejae.tenniverse.persentation.login

import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentGenderBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.util.append8
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderFragment: BaseFragment<FragmentGenderBinding>(R.layout.fragment_gender) {
    private val viewModel: MainViewModel by activityViewModels()
    override fun initView() {
        initBinding()
        setListener()
    }
    private fun setListener() {
        binding.btnNext.setOnClickListener {
            (requireActivity() as LoginActivity).nextPager(3)
        }
    }
    private fun initBinding() {
        binding.viewModel = viewModel

        binding.tvDesCheckUser.text = buildSpannedString {
            append8(requireActivity(), text = "원활한 팀 매칭을 위해\n")
            append8(requireActivity(), color = R.color.green, text = "당신의 성별")
            append8(requireActivity(), text = "을 알려 주세요!")
        }
    }
    companion object {
        fun newInstance() = GenderFragment()
    }
}