package com.heejae.tenniverse.util

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider

interface PhoneAuthCallback {
    fun onSendCodeResult(verificationId: String, token: PhoneAuthProvider.ForceResendingToken)
    fun onSignInCredentialResult(user: FirebaseUser)
}