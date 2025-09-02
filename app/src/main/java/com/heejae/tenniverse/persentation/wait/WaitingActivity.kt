package com.heejae.tenniverse.persentation.wait

import androidx.activity.viewModels
import androidx.core.text.buildSpannedString
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentWaitingBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import com.heejae.tenniverse.util.append8
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitingActivity: BaseActivity<FragmentWaitingBinding>(R.layout.fragment_waiting) {

    private val viewModel: WaitingViewModel by viewModels()
    override fun setViewModel() = viewModel

    override fun initView() {
        binding.tvWaiting.text = buildSpannedString {
            append8(this@WaitingActivity, text = "테니버스 가입 대기중...")
        }

        binding.tvDesWaiting.text = buildSpannedString {
            append8(this@WaitingActivity, font = R.font.noto_sans_semi_bold, text = "앱 관리자가 ")
            append8(
                this@WaitingActivity,
                color = R.color.green,
                font = R.font.noto_sans_semi_bold,
                text = "가입을 승인"
            )
            append8(
                this@WaitingActivity,
                font = R.font.noto_sans_semi_bold,
                text = "시켜줄 때까지\n잠시만 기다려주세요~!"
            )
        }

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_none)
        binding.btnNext.setOnClickListener {
            finish()
        }
    }
}