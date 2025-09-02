package com.heejae.tenniverse.persentation.home.management

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val masterRepository: MasterRepository
) : BaseViewModel() {

    private val _userList = MutableStateFlow<PagingData<UserModel>>(PagingData.empty())
    val userList get() = _userList.asStateFlow()

    init {
        getUserPaging()
    }

    fun getUserPaging() {
        masterRepository.getUser(true).cachedIn(viewModelScope).onEach {
            _userList.value = it
            finished()
        }.launchIn(viewModelScope)
    }

    fun setUserRate(model: UserModel) {
        loading()
        viewModelScope.launch {
            masterRepository.updateUserRate(model)
            getUserPaging()
        }
    }

    fun removeUser(model: UserModel) {
        loading()
        viewModelScope.launch {
            masterRepository.removeUser(model)
            getUserPaging()
        }
    }
}