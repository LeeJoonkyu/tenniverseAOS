package com.heejae.tenniverse.persentation.home.rent.event

import android.widget.Toast
import androidx.activity.viewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentRentEventBinding
import com.heejae.tenniverse.persentation.home.rent.common.EventActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventRentActivity: EventActivity<FragmentRentEventBinding>(R.layout.fragment_rent_event) {
    override val viewModel : EventViewModel by viewModels()

    override fun initBinding() {
        binding.viewModel = viewModel
    }

    override fun eventInitView() {
        setListener()
    }

    override fun setViewModel() = viewModel

    private fun setListener() {
        binding.tvDate.setOnClickListener {
            datePicker.show(supportFragmentManager,null)
        }
        binding.tvTime.setOnClickListener {
            timePicker.show(supportFragmentManager, null)
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
        binding.btnNext.setOnClickListener {
            viewModel.createRent(
                onSuccess = {
                    setResult()
                },
                onFailure = {
                    Toast.makeText(this, "현재 시간 이후로 등록 가능합니다!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}