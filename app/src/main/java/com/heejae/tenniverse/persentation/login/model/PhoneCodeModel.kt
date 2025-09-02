package com.heejae.tenniverse.persentation.login.model

import com.google.firebase.auth.PhoneAuthProvider

data class PhoneCodeModel(
    val verificationId: String,
    val token: PhoneAuthProvider.ForceResendingToken,
)
