package com.heejae.tenniverse.persentation.login

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.heejae.tenniverse.domain.model.CredentialState
import com.heejae.tenniverse.persentation.login.model.PhoneCodeModel

@BindingAdapter(value = ["setUserState", "setCodeModel","helper", "error"])
fun TextInputLayout.setInputLayout(
    userState: CredentialState,
    codeModel: PhoneCodeModel?,
    helper: String,
    error: String
) {
    when(userState) {
        is CredentialState.Success.SignInCredential -> {
            helperText = helper
        }
        is CredentialState.Failure -> {
            this.error = if (codeModel == null) null else error
        }
        else -> {
            helperText = null
            this.error = null
        }
    }
}