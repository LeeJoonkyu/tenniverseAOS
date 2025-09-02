package com.heejae.tenniverse.persentation.home.rent.common

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.CourtType
import com.heejae.tenniverse.domain.model.GameType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.calendar.day
import com.heejae.tenniverse.util.calendar.dayTime
import com.heejae.tenniverse.util.calendar.getDateFormat
import com.heejae.tenniverse.util.calendar.getTimeFormat
import com.heejae.tenniverse.util.calendar.month
import com.heejae.tenniverse.util.calendar.updateFormat
import com.heejae.tenniverse.util.calendar.year
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

open class RentCommonViewModel(
    private val auth: FirebaseAuth,
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {
    /**
     *  연락처 11자 ,이름 두자 이상 ,장소 세자 이상 ,날짜 오늘 이후, 시간 1이상 ,인원 2 이상 ,비용 1원 이상
     */

    protected val uid = auth.uid

    private val _userModel = MutableStateFlow(UserModel())
    val userModel get() = _userModel.asStateFlow()

    private var _calendar = MutableStateFlow<Calendar>(Calendar.getInstance(Locale.getDefault()))
    var calendar = _calendar.asStateFlow()

    var dayTime = MutableStateFlow(calendar.value.dayTime())
    var dateFormat = MutableStateFlow(calendar.value.getDateFormat())
    var timeFormat = MutableStateFlow(calendar.value.getTimeFormat())

    val name = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val place = MutableStateFlow("")
    val totalTime = MutableStateFlow("")
    val maxMember = MutableStateFlow("")
    val countCourt = MutableStateFlow("")
    val costCourt = MutableStateFlow("")
    val gameType = MutableStateFlow(GameType.MAN_SINGLE)
    val courtType = MutableStateFlow(CourtType.CLAY)
    val bank = MutableStateFlow("")
    val account = MutableStateFlow("")
    val des = MutableStateFlow("")

    val validationName = MutableStateFlow(false)
    val validationPhone = MutableStateFlow(false)
    val validationPlace = MutableStateFlow(false)
    val validationTotalTime = MutableStateFlow(false)
    val validationMaxMember = MutableStateFlow(false)
    val validationCostCourt = MutableStateFlow(false)
    val validationTime = MutableStateFlow(false)

    val rentEnable = MutableStateFlow(false)
    val regularEnable = MutableStateFlow(false)
    val eventEnable = MutableStateFlow(false)

    val uploadDate get() = calendar.value.updateFormat()

    init {
        initUser()
    }

    private fun initUser() {
        viewModelScope.launch {
            _userModel.value = userRepository.currentUser() ?: return@launch
        }
    }

    fun setDateModel(calendar: Calendar) {
        _calendar.value = this.calendar.value.apply {
            set(Calendar.YEAR, calendar.year())
            set(Calendar.MONTH, calendar.month() - 1)
            set(Calendar.DAY_OF_MONTH, calendar.day())
        }
        setFormatter()
    }

    fun setDateModel(hour: Int, minute: Int) {
        _calendar.value = calendar.value.apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        setFormatter()
    }

    private fun setFormatter() {
        calendar.value.run {
            dayTime.value = dayTime()
            dateFormat.value = getDateFormat()
            timeFormat.value = getTimeFormat()
        }
        setEnable()
    }
    fun setName(name: String) {
        this.name.value = name
        validationName()
    }
    fun setPhone(phone: String) {
        this.phone.value = phone
        validationPhone()
    }

    fun validationName() {
        validationName.value = name.value.length >= 2
        setEnable()
    }

    fun validationPhone() {
        validationPhone.value = phone.value.length == 11
        setEnable()
    }

    fun validationPlace() {
        validationPlace.value = place.value.length >= 3
        setEnable()
    }

    fun validationTotalTime() {
        validationTotalTime.value = totalTime.value.isNotEmpty() && totalTime.value.toInt() >= 1
        setEnable()
    }

    fun validationMaxMember() {
        validationMaxMember.value = maxMember.value.isNotEmpty() && maxMember.value.toInt() >= 2
        setEnable()
    }

    fun validationCountCourt() {}
    fun validationCost() {
        validationCostCourt.value = costCourt.value.isNotEmpty() && costCourt.value.toInt() > 1
        setEnable()
    }

    fun validationBank() {}
    fun validationAccount() {}

    fun checkDate() = calendar.value.timeInMillis > Calendar.getInstance(Locale.getDefault()).timeInMillis


    protected suspend fun task(onStartTask: suspend () -> Unit) {
        _userModel.value = userRepository.currentUser() ?: return
        onStartTask()
    }

    suspend fun update(rent: Rent) {
        uid ?: return
        task {
            val rentUid = rentRepository.uploadRent(rent)
            userRepository.updateUserReservations(uid, userModel.value.reservations.apply {
                put(rentUid, uploadDate)
            })
        }
    }

    private fun setEnable() {
        rentEnable.value = validationName.value && validationPhone.value
                && validationPlace.value && validationTotalTime.value
                && validationMaxMember.value && validationCostCourt.value

        regularEnable.value = validationPlace.value && validationTotalTime.value
                && validationMaxMember.value && validationCostCourt.value

        eventEnable.value = validationName.value && validationPhone.value
                && validationPlace.value && validationTotalTime.value
                && validationMaxMember.value
    }


}