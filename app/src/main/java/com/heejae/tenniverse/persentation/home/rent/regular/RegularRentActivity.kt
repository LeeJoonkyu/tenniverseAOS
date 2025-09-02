package com.heejae.tenniverse.persentation.home.rent.regular

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentRegularRentBinding
import com.heejae.tenniverse.persentation.home.rent.common.RentCommonActivity
import com.heejae.tenniverse.persentation.home.rent.regular.choice.RentUserChoiceActivity
import com.heejae.tenniverse.util.PUT_EXTRA_RENT
import com.heejae.tenniverse.util.PUT_EXTRA_WEEKLY_CREATE_COUNT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegularRentActivity: RentCommonActivity<FragmentRegularRentBinding>(R.layout.fragment_regular_rent) {
    override val viewModel : RentRegularViewModel by viewModels()

    override fun initBinding() {
        binding.viewModel = viewModel
    }
    override fun rentInitView() {
        setListener()
    }

    override fun setViewModel() = viewModel

    override fun getGenderACTV() = binding.etDoublesGender
    override fun getCoatACTV() = binding.etCoatType

    private fun setListener() {
        binding.btnNext.setOnClickListener {

            viewModel.createRent(
                onSuccess = { rent, createCount ->
                    startActivity(
                        Intent(this, RentUserChoiceActivity::class.java).apply {
                            putExtra(PUT_EXTRA_RENT, rent)
                            putExtra(PUT_EXTRA_WEEKLY_CREATE_COUNT, createCount)
                        }
                    )
                },
                onFailure = {
                    Toast.makeText(this, "현재 시간 이후로 등록 가능합니다!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        binding.tvDate.setOnClickListener {
            datePicker.show(supportFragmentManager,null)
        }
        binding.tvTime.setOnClickListener {
            timePicker.show(supportFragmentManager, null)
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
    }
}