package com.heejae.tenniverse.persentation.dialog

import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.DialogTenniverseBinding
import com.heejae.tenniverse.domain.model.DialogData
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.persentation.base.BaseDialog

class CustomDialog(
    val type: DialogType,
    val onSuccess: () -> Unit,
): BaseDialog<DialogTenniverseBinding>(R.layout.dialog_tenniverse) {
    override fun initView() {
        initData()
        setClickListener()
    }

    private fun initData() {
        binding.model = when(type) {
            DialogType.DELETE_USER -> {
                DialogData(
                    getString(R.string.dialog_title_user_delete),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.dialog_pos_user_delete)
                )
            }
            DialogType.DELETE_RENT_USER -> {
                DialogData(
                    getString(R.string.dialog_title_delete_rent_user),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.DELETE_RENT -> {
                DialogData(
                    getString(R.string.dialog_title_delete_rent),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.DEADLINE_RECRUIT -> {
                DialogData(
                    getString(R.string.dialog_title_deadline_recruit),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.CANCEL_PARTICIPATE -> {
                DialogData(
                    getString(R.string.dialog_title_cancel_participate),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.ASK_PARTICIPATE -> {
                DialogData(
                    getString(R.string.dialog_title_ask_participate),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.DELETE_ACCOUNT -> {
                DialogData(
                    getString(R.string.dialog_title_ask_delete_account),
                    getString(R.string.dialog_neg_user_delete),
                    getString(R.string.yes)
                )
            }
            DialogType.VERSION_CHECK -> {
                binding.btnNo.isGone = true
                binding.btnOk.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_border_dialog_btn)
                binding.div.isGone = true
                DialogData(
                    getString(R.string.dialog_title_ask_version_check),
                    "확인",
                    "확인"
                )
            }
        }
        binding.btnOk.setOnClickListener {
            onSuccess()
        }
    }

    private fun setClickListener() {
        binding.btnOk.setOnClickListener {
            onSuccess()
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }
}