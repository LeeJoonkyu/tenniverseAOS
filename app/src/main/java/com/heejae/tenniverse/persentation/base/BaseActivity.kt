package com.heejae.tenniverse.persentation.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.heejae.tenniverse.persentation.dialog.ProgressDialog
import com.heejae.tenniverse.R
import com.heejae.tenniverse.domain.model.DialogType
import com.heejae.tenniverse.domain.model.UiState
import com.heejae.tenniverse.persentation.dialog.CustomDialog
import com.heejae.tenniverse.persentation.home.HomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {

    lateinit var binding: B
    protected lateinit var progressDialog: ProgressDialog
    private lateinit var baseViewModel: BaseViewModel

    protected lateinit var ime: InputMethodManager

    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        initViewModel()
        initIme()
        initView()
        initProgressbar()
        viewModelObserver()
        setListener()
    }

    protected fun initViewModel() {
        baseViewModel = setViewModel()
    }

    protected fun showDialog(type: DialogType, onSuccess: () -> Unit) {
        val dialog = CustomDialog(
            type,
            onSuccess = onSuccess
        )
        dialog.show(supportFragmentManager, "")
    }

    abstract fun setViewModel(): BaseViewModel

    private fun initProgressbar() {
        progressDialog = ProgressDialog(this)
    }

    private fun initIme() {
        ime = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    protected fun hideKeyboard() {
        ime.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun setListener() {
        binding.root.setOnClickListener {
            Log.d(this.javaClass.name, "setListener")
            hideKeyboard()
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
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
                        if (it == UiState.Error) {
                            animationFinished()
                        }
                    }
                }
            }
        }
    }

    abstract fun initView()

    protected fun goToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
        animationFinished()
    }

    protected fun setResult() {
        setResult(Activity.RESULT_OK)
        animationFinished()
    }

    open fun handleBackPressed() {
        animationFinished()
    }

    fun animationFinished() {
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onDestroy() {
        progressDialog.closeDialog()
        super.onDestroy()
    }
}
