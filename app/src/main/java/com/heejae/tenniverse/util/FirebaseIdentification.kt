package com.heejae.tenniverse.util

import android.app.Activity
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.heejae.tenniverse.domain.model.CredentialState
import com.heejae.tenniverse.persentation.login.model.PhoneCodeModel
import com.heejae.tenniverse.persentation.main.MainViewModel
import java.util.concurrent.TimeUnit

class PhoneAuthManager(
    val activity: Activity,
    private val auth: FirebaseAuth,
    val viewModel: MainViewModel
) {
    private lateinit var options: PhoneAuthOptions

    fun requestCode() {
        Log.d(this.javaClass.name, "phone: ${viewModel.phoneFormatting}")
        viewModel.setUserState(CredentialState.Loading)
        options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(viewModel.phoneFormatting) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            DEBUG(this@PhoneAuthManager.name, "onVerificationCompleted credential: $credential")
            viewModel.setUserState(CredentialState.Loading)
            viewModel.setCertificationNumber(credential.smsCode ?: return)
            viewModel.phoneAuthCredential = credential
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            DEBUG(this@PhoneAuthManager.name, "onVerificationFailed $e")
            viewModel.setUserState(CredentialState.Failure(e))
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            /**
             *  이 메서드는 제공된 전화번호로 인증 코드가 SMS를 통해 전송된 후에 호출됩니다.
             */
            DEBUG(this@PhoneAuthManager.name, "onCodeSent id: $verificationId token: $token")
            val model = PhoneCodeModel(verificationId, token)
            viewModel.setUserState(CredentialState.Success.SendCode(model))
            viewModel.setCodeModel(model)
        }
    }

    var flag = true

    fun signInWithPhoneAuthCredential() {
        val id = viewModel.phoneCodeModel.value?.verificationId ?: ""
        val code = viewModel.certificationNumber.value

        viewModel.setUserState(CredentialState.Loading)
        signInWithPhoneAuthCredential(
            viewModel.phoneAuthCredential ?: PhoneAuthProvider.getCredential(id, code)
        )
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        // TODO : 기존에 가입된 유저인지 체크 해야함.

        DEBUG(
            this@PhoneAuthManager.name,
            "credential: id: ${credential.provider} code: ${credential.smsCode}"
        )

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user ?: return@addOnCompleteListener
                    Log.d(this.javaClass.name, "user: ${user.uid}")

                    viewModel.checkRegisteredUser(user)
                } else {
                    viewModel.setUserState(
                        CredentialState.Failure(
                            task.exception ?: return@addOnCompleteListener
                        )
                    )
                }
            }
    }
}