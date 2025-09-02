package com.heejae.tenniverse.persentation.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.heejae.tenniverse.R

class ProgressDialog(val context: Context) {
    private lateinit var dialog: Dialog

    init {
        initDialog()
    }

    private fun initDialog() {
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.progressbar)
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    android.R.color.transparent
                )
            )
            window?.setDimAmount(0.6f)
            setCancelable(false)
        }
        dialog.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    fun showDialog() {
        dialog.show()
    }

    fun closeDialog() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }
}