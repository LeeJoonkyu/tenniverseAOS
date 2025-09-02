package com.heejae.tenniverse.persentation.home.rent.regular.choice

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.heejae.tenniverse.data.MasterRepository
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_RENT
import com.heejae.tenniverse.util.PUT_EXTRA_WEEKLY_CREATE_COUNT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RentUserViewModel @Inject constructor(
    private val masterRepository: MasterRepository,
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val rent = savedStateHandle.get<Rent>(PUT_EXTRA_RENT) ?: Rent()
    private val createCount = savedStateHandle.get<Int>(PUT_EXTRA_WEEKLY_CREATE_COUNT) ?: 0

    private val _userList = MutableStateFlow<PagingData<UserModel>>(PagingData.empty())
    val userList get() = _userList.asStateFlow()

    private val _selected = MutableStateFlow<List<UserModel>>(listOf())
    val selected = _selected.asStateFlow()

    val btnValidation = MutableStateFlow(false)

    init {
        getUser()
    }

    private fun getUser() {
        masterRepository.getUser(true).cachedIn(viewModelScope).onEach {
            _userList.value = it.filter { it.displayName.isNotEmpty() }
        }.launchIn(viewModelScope)
    }

    fun setSelectedList(selected: List<UserModel>) {
        DEBUG(this@RentUserViewModel.name, "selected: $selected")
        _selected.value = selected.toList()
        btnValidation.value = selected.isNotEmpty()
    }

    fun accept(onSuccess: () -> Unit) = start(onSuccess) {
        DEBUG(this@RentUserViewModel.name, "rent: $rent")
        val rentList = rentRepository.createWeeklyRent(selected.value, rent, createCount)

        DEBUG(this@RentUserViewModel.name, "rentList: $rentList")
        DEBUG(this@RentUserViewModel.name, "selected: ${selected.value}")

        userRepository.updateWeeklyRentUsers(selected.value, rentList)
    }
}