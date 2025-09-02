package com.heejae.tenniverse.persentation.home.newuser

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.heejae.tenniverse.data.MasterRepository
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NewUserViewModel @Inject constructor(
    private val masterRepository: MasterRepository
) : BaseViewModel() {

    private val _userList = MutableStateFlow<PagingData<UserModel>>(PagingData.empty())
    val userList get() = _userList.asStateFlow()

    private val _selected = MutableStateFlow<List<UserModel>>(listOf())
    val selected = _selected.asStateFlow()

    val btnValidation = MutableStateFlow(false)

    init {
        getUser()
    }

    private fun getUser() {
        masterRepository.getUser(false).cachedIn(viewModelScope).onEach {
            _userList.value = it
        }.launchIn(viewModelScope)
    }

    fun setSelectedList(selected: List<UserModel>) {
        _selected.value = selected
        btnValidation.value = selected.isNotEmpty()
    }

    fun accept(onSuccess: () -> Unit) = start(onSuccess) {
        masterRepository.acceptUser(selected.value)
    }

    fun reject(onSuccess: () -> Unit) = start(onSuccess) {
        masterRepository.rejectUser(selected.value)
    }
}