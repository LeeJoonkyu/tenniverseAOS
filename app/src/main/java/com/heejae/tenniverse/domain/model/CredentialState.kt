package com.heejae.tenniverse.domain.model

import com.google.firebase.auth.FirebaseUser
import com.heejae.tenniverse.persentation.login.model.PhoneCodeModel
import java.lang.Exception

sealed interface CredentialState {
    object UnInitialized : CredentialState
    object Loading : CredentialState
    sealed interface Success : CredentialState {
        data class SendCode(val phoneCodeModel: PhoneCodeModel) : Success
        data class SignInCredential(val user: FirebaseUser?, val isRegisteredUser: Boolean, val isMember: Boolean) :
            Success
    }

    data class Failure(val exception: Exception) : CredentialState
}
