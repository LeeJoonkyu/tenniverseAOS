package com.heejae.tenniverse.persentation.home.user

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import com.heejae.tenniverse.util.calendar.dbDateToTime
import com.heejae.tenniverse.util.calendar.month
import com.heejae.tenniverse.util.calendar.year
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val initUserModel = savedStateHandle.get<UserModel>(PUT_EXTRA_USER) ?: UserModel()

    private val _userModel = MutableStateFlow(initUserModel)
    val userModel get() = _userModel.asStateFlow()

    private val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())

    val year = MutableStateFlow(currentCalendar.year().toString())
    val month = MutableStateFlow(currentCalendar.month().toString())

    private val _reservations = MutableStateFlow<List<RentModel>>(listOf())
    val reservations get() = _reservations.asStateFlow()

    init {
        DEBUG(this@MyPageViewModel.name, "userModel: $initUserModel")
        initData()
    }

    fun notLoadingInitData() {
        viewModelScope.launch {
            _userModel.value = userRepository.currentUser() ?: return@launch
            getRentModelList()
        }
    }

    private fun initData() = start {
        notLoadingInitData()
    }

    private suspend fun getRentModelList() {
        _reservations.value = rentRepository.getRents(getRentList())
    }

    fun nextMonth() {
        currentCalendar.add(Calendar.MONTH, 1)
        updateDate()
    }

    fun prevMonth() {
        currentCalendar.add(Calendar.MONTH, -1)
        updateDate()
    }

    private fun updateDate() = start {
        year.value = currentCalendar.year().toString()
        month.value = currentCalendar.month().toString()
        getRentModelList()
    }

    private fun getRentList() = userModel.value.reservations.toList()
        .filter {
            val calendar = getCalendar(it.second)
            year.value.toInt() == calendar.year() &&
                    month.value.toInt() == calendar.month()
        }
        .map {
            it.first
        }

    private fun getCalendar(date: String): Calendar =
        Calendar.getInstance().apply {
            timeInMillis = date.dbDateToTime()
        }
}