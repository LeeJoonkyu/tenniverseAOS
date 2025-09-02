package com.heejae.tenniverse.persentation.splash

import androidx.lifecycle.viewModelScope
import com.heejae.tenniverse.data.MasterRepository
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.persentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val masterRepository: MasterRepository
): BaseViewModel() {

    fun checkUser(onSuccess: (isRegistered: Boolean, isMember: Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.currentUser()
            onSuccess(user != null, user?.registered ?: false)
        }
    }
    fun checkPrevVersion(prev: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (masterRepository.checkPrevVersion()) {
                prev()
            }else {
                onSuccess()
            }
        }
    }
}