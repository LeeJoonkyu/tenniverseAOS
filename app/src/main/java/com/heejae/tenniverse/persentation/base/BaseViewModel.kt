package com.heejae.tenniverse.persentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heejae.tenniverse.domain.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {

    protected val _uiState = MutableStateFlow<UiState>(UiState.UnInitialized)
    val uiState get() = _uiState.asStateFlow()

    fun finished() {
        _uiState.value = UiState.UnInitialized
    }
    fun loading() {
        _uiState.value = UiState.Loading
    }
    fun error() {
        _uiState.value = UiState.Error
    }

    protected fun start(onSuccess: () -> Unit = {}, task: suspend () -> Unit) {
        viewModelScope.launch {
            loading()
            task()
            finished()
            onSuccess()
        }
    }
}