package com.heejae.tenniverse.persentation.login

import androidx.core.text.buildSpannedString
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentWaitingBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.util.append8
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitingFragment : BaseFragment<FragmentWaitingBinding>(R.layout.fragment_waiting) {
    override fun initView() {

        binding.tvWaiting.text = buildSpannedString {
            append8(requireActivity(), text = "테니버스 가입 대기중...")
        }

        binding.tvDesWaiting.text = buildSpannedString {
            append8(requireActivity(), font = R.font.noto_sans_semi_bold, text = "앱 관리자가 ")
            append8(
                requireActivity(),
                color = R.color.green,
                font = R.font.noto_sans_semi_bold,
                text = "가입을 승인"
            )
            append8(
                requireActivity(),
                font = R.font.noto_sans_semi_bold,
                text = "시켜줄 때까지\n잠시만 기다려주세요~!"
            )
        }


        binding.btnNext.setOnClickListener {
            requireActivity().finish()
        }
    }
}