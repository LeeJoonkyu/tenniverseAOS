package com.heejae.tenniverse.persentation.home.rentdetail.comment

import android.content.ClipData
import android.content.ClipboardManager
import androidx.activity.viewModels
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.ActivityRentDetailCommentBinding
import com.heejae.tenniverse.persentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RentDetailCommentActivity :
    BaseActivity<ActivityRentDetailCommentBinding>(R.layout.activity_rent_detail_comment) {

    private val viewModel: RentDetailCommentViewModel by viewModels()
    override fun setViewModel() = viewModel

    override fun initView() {
        initBinding()
        setListener()
    }

    private fun setListener() {
        binding.btnStandard.setOnClickListener {
            viewModel.recruit {
                setResult()
            }
        }
        binding.ivBack.setOnClickListener {
            handleBackPressed()
        }
        binding.tvCopy.setOnClickListener {
            (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
                setPrimaryClip(
                    ClipData.newPlainText(null, viewModel.rentModel.value.ownerAccount)
                )
            }
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.model = viewModel.rentModel.value
    }
}