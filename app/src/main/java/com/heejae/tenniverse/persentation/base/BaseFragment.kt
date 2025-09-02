package com.heejae.tenniverse.persentation.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.heejae.tenniverse.domain.model.UiState
import com.heejae.tenniverse.persentation.dialog.ProgressDialog
import com.heejae.tenniverse.persentation.login.LoginActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseFragment<B : ViewDataBinding>(
    @LayoutRes val layoutId: Int
) : Fragment() {

    private var _binding: B? = null
    val binding get() = _binding ?: error("null binding")

    protected lateinit var ime: InputMethodManager

    protected lateinit var callback: OnBackPressedCallback
    protected lateinit var progressDialog: ProgressDialog
    private lateinit var baseViewModel: BaseViewModel

    fun isProgressDialogInitialized() = ::progressDialog.isInitialized

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as LoginActivity).backPager()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initIme()
        setListener()
        initView()
        initProgressbar()
        initBackPressed()
        viewModelObserver()
    }
    protected fun initViewModel(viewModel: BaseViewModel) {
        baseViewModel = viewModel
    }

    private fun initBackPressed() {
        callback.isEnabled = requireActivity() is LoginActivity
    }

    private fun initProgressbar() {
        progressDialog = ProgressDialog(requireActivity())
    }

    private fun viewModelObserver() {
        if (::baseViewModel.isInitialized) {
            lifecycleScope.launch {
                baseViewModel.uiState.collectLatest {
                    if (it == UiState.Loading) {
                        progressDialog.showDialog()
                    } else {
                        progressDialog.closeDialog()
                        baseViewModel.finished()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        progressDialog.closeDialog()
    }

    protected fun setFocusing(view: View, flags: Int) {
        view.apply {
            requestFocus()
            ime.showSoftInput(this, flags)
        }
    }

    protected fun hideKeyboard() {
        ime.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,
            0
        )
    }

    private fun setListener() {
        binding.root.setOnClickListener {
            Log.d(this.javaClass.name, "setListener")
            hideKeyboard()
        }
    }

    private fun initIme() {
        ime =
            requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    abstract fun initView()
}
