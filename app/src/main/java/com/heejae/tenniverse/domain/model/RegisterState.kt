package com.heejae.tenniverse.domain.model

import java.lang.Exception

sealed interface RegisterState {
    object UnInitialized: RegisterState
    object Loading: RegisterState
    object Success: RegisterState
    data class Failure(val exception: Exception): RegisterState
}
