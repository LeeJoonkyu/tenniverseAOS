package com.heejae.tenniverse.persentation.home.rent.day

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentRentBinding
import com.heejae.tenniverse.persentation.home.rent.common.RentCommonActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RentActivity: RentCommonActivity<FragmentRentBinding>(R.layout.fragment_rent) {
    override val viewModel: RentViewModel by viewModels()
    override fun rentInitView() {
        setViewModelObserver()
        setListener()
    }

    override fun setViewModel() = viewModel

    override fun getGenderACTV() = binding.etDoublesGender
    override fun getCoatACTV() = binding.etCoatType

    private fun setViewModelObserver() {
        lifecycleScope.launch {

        }
    }

    private fun setListener() {
        binding.tvDate.setOnClickListener {
            datePicker.show(supportFragmentManager,null)
        }
        binding.tvTime.setOnClickListener {
            timePicker.show(supportFragmentManager, null)
        }
        binding.clRoot.setOnClickListener {
            hideKeyboard()
        }
        binding.ivBack.setOnClickListener {
            animationFinished()
        }
        binding.btnRentCreate.setOnClickListener {
            viewModel.createRent(
                onSuccess = {
                    DEBUG(this@RentActivity.name, "btnRentCreate onSuccess")
                    setResult()
                },
                onFailure = {
                    Toast.makeText(this, "현재 시간 이후로 등록 가능합니다!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun initBinding() {
        binding.viewModel = viewModel
    }
}