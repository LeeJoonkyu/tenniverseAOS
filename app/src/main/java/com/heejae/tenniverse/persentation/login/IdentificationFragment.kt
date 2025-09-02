package com.heejae.tenniverse.persentation.login

import android.content.Intent
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.heejae.tenniverse.R
import com.heejae.tenniverse.databinding.FragmentIdentificationBinding
import com.heejae.tenniverse.persentation.base.BaseFragment
import com.heejae.tenniverse.domain.model.CredentialState
import com.heejae.tenniverse.persentation.home.HomeActivity
import com.heejae.tenniverse.persentation.main.MainViewModel
import com.heejae.tenniverse.persentation.wait.WaitingActivity
import com.heejae.tenniverse.util.PhoneAuthManager
import com.heejae.tenniverse.util.append8
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IdentificationFragment :
    BaseFragment<FragmentIdentificationBinding>(R.layout.fragment_identification) {

    private val viewModel: MainViewModel by activityViewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun initView() {
        initBinding()
        setViewModelObserve()
        setListener()
    }

    override fun onResume() {
        super.onResume()
        setFocusing(binding.etPhone, 0)
    }

    private fun setViewModelObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.credentialState.collectLatest { state ->

                    if (state == CredentialState.Loading) {
                        Log.d(this@IdentificationFragment.javaClass.name, "Loading")
                        progressDialog.showDialog()
                        return@collectLatest
                    }

                    when (state) {
                        is CredentialState.UnInitialized -> {}
                        is CredentialState.Success -> loginSuccessHandle(state)
                        is CredentialState.Failure -> loginFailureHandle(state)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun loginSuccessHandle(state: CredentialState.Success) {
        when (state) {
            is CredentialState.Success.SendCode -> {
                setFocusing(binding.etCert, InputMethodManager.SHOW_IMPLICIT)
            }

            is CredentialState.Success.SignInCredential -> {

                DEBUG(this@IdentificationFragment.name, "state: $state")

                if (state.isRegisteredUser) {
                    if (state.isMember) {
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
                    }else {
                        startActivity(Intent(requireActivity(), WaitingActivity::class.java))
                    }
                    requireActivity().finish()
                    return
                }

                viewModel.setUser(state.user ?: return)
                (requireActivity() as LoginActivity).nextPager(1)
                viewModel.setUserState(CredentialState.UnInitialized)
                hideKeyboard()
            }
        }
        progressDialog.closeDialog()
    }

    private fun loginFailureHandle(state: CredentialState.Failure) {
        Log.d(this.javaClass.name, "state: ${state.exception}")
        when (state.exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                // Invalid request
            }

            is FirebaseTooManyRequestsException -> {
                // The SMS quota for the project has been exceeded
                Toast.makeText(requireActivity(), "너무 많이 요청하셨습니다. 잠시후에 시도해주세요", Toast.LENGTH_SHORT)
                    .show()
            }

            is FirebaseAuthMissingActivityForRecaptchaException -> {
                // reCAPTCHA verification attempted with null Activity
            }

            else -> {
                // nothing
            }
        }
        viewModel.clearCertificationNumber()
        progressDialog.closeDialog()
    }

    private fun setListener() {
        binding.btnNext.setOnClickListener {
            (requireActivity() as LoginActivity).nextPager(1)
        }
    }

    private fun initBinding() {
        binding.viewModel = viewModel
        binding.phoneAuthManager = PhoneAuthManager(requireActivity(), auth, viewModel)

        binding.tvDesCheckUser.text = buildSpannedString {
            append8(requireActivity(), text = "본인 확인을 위해\n")
            append8(requireActivity(), color = R.color.green, text = "당신의 번호")
            append8(requireActivity(), text = "를 알려 주세요!")
        }
    }

    companion object {
        fun newInstance() = IdentificationFragment()
    }
}