package com.heejae.tenniverse.persentation.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialog<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
): DialogFragment() {

    private var _binding: B? = null
    val binding get() = _binding ?: error("null binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        DEBUG(this@BaseDialog.name, "TEST_DIALOG : $_binding")
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.apply {
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        initView()
    }

    abstract fun initView()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}