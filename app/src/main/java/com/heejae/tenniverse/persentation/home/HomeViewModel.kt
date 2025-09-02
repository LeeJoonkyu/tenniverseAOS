package com.heejae.tenniverse.persentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.domain.model.UserType
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.calendar.dbDateToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    private val _selectedRent = MutableStateFlow(RentType.ALL)
    val selectedRent get() = _selectedRent.asStateFlow()

    private val _rentList = MutableStateFlow<PagingData<RentModel>>(PagingData.empty())
    val rentList = _rentList.asStateFlow()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user get() = _user.asStateFlow()

    private val _reservations = MutableStateFlow<List<RentModel>>(listOf())
    val reservations get() = _reservations.asStateFlow()

    init {
        getRents()
        getReservation()
        updateUserToken()
    }

    private fun updateUserToken() {
        viewModelScope.launch {
            userRepository.updateUserToken()
        }
    }

    fun refresh(onSuccess: () -> Unit = {}) = start {
        getRents(onSuccess)
        updateReservation()
    }

    private fun getReservation() {
        viewModelScope.launch {
            _user.value = userRepository.currentUser() ?: return@launch
            val map = user.value?.reservations ?: hashMapOf()

            val currentTime = System.currentTimeMillis()
            ArrayList(map.entries)
                .sortedBy { it.value }
                .filter {
                    it.value.dbDateToTime() > currentTime
                }
                .forEach { (uid, _) ->
                    _reservations.value = reservations.value.toMutableList().apply {
                        val rent = rentRepository.getRent(uid) ?: return@apply
                        if (rent.member[user.value?.uid] != null) {
                            add(rent)
                        }
                    }
                }
        }
    }

    private fun updateReservation() {
        viewModelScope.launch {
            _user.value = userRepository.currentUser() ?: return@launch
            _reservations.value = rentRepository.getRentOfUsers(
                user.value?.uid,
                user.value?.reservations ?: hashMapOf()
            )
        }
    }

    private fun getRents(onSuccess: () -> Unit = {}) {
        _rentList.value = PagingData.empty()
        rentRepository.getRents(selectedRent.value).cachedIn(viewModelScope).onEach {
            _rentList.value = it
            onSuccess()
        }.launchIn(viewModelScope)
    }

    fun setSelectedRent(position: Int) {
        _rentList.value = PagingData.empty()
        _selectedRent.value = rentTypeList[position]
        getRents()
    }

    fun checkRent(uid: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (rentRepository.getRent(uid) != null) {
                onSuccess()
            } else {
                // TODO : 존재 하지 않는 Rent
            }
        }
    }

    companion object {
        val rentTypeList = RentType.values()
    }
}