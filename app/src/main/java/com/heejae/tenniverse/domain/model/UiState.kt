package com.heejae.tenniverse.domain.model

import java.lang.Exception

sealed interface UiState {
    object UnInitialized: UiState
    object Loading: UiState
    object Success: UiState
    data class Failure(val exception: Exception): UiState
    object Error: UiState
}