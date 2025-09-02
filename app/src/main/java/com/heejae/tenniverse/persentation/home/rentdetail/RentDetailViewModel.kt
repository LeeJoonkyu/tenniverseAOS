package com.heejae.tenniverse.persentation.home.rentdetail

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.system.Os.remove
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.UserRentModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_FROM_FCM_RENT_UID
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_MODEL
import com.heejae.tenniverse.util.calendar.enableWeeklyParticipated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RentDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val rentRepository: RentRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val initRentModel =
        savedStateHandle.get<RentModel>(PUT_EXTRA_RENT_MODEL) ?: RentModel("")
    val rentIdFromFCM = savedStateHandle.get<String?>(PUT_EXTRA_FROM_FCM_RENT_UID)

    val rentModel = MutableStateFlow(initRentModel)

    private val myUid = auth.uid ?: ""
    val isBeforeTwoWeek = MutableStateFlow(checkIsBeforeTwoWeek())
    val userType = MutableStateFlow(rentModel.value.getUserType(myUid))
    val isMember = MutableStateFlow(rentModel.value.isParticipated(myUid))
    val isRoot = rentModel.value.ownerId == myUid

    private val _members = MutableStateFlow<List<UserRentModel>>(listOf())
    val members get() = _members.asStateFlow()

    private val _waiting = MutableStateFlow<List<UserRentModel>>(listOf())
    val waiting get() = _waiting.asStateFlow()

    val isAccountOpened = MutableStateFlow(true)
    val isAskWantToParticipate = MutableStateFlow(AskWantToParticipate(false))

    init {
        initData()
    }

    fun initData(
        uid: String = rentIdFromFCM ?: rentModel.value.uid,
        onSuccess: () -> Unit = {}
    ) = start {
        task(uid) {
            _members.value =
                userRepository.getMembers(rentModel.value.uid, rentModel.value.member)
                    .toMutableList().apply {
                        val root = find { it.uid == rentModel.value.ownerId } ?: return@apply
                        val idx = indexOf(root)
                        if (idx == 0) {
                            set(0, root.copy(root = true))
                            return@apply
                        }
                        val first = this[0]
                        set(0, root.copy(root = true))
                        set(idx, first)
                    }
            _waiting.value =
                userRepository.getWaiting(rentModel.value.uid, rentModel.value.waitings)
            userType.value = rentModel.value.getUserType(myUid)
            isMember.value = rentModel.value.isParticipated(myUid)
            isBeforeTwoWeek.value = checkIsBeforeTwoWeek()
            askWantToParticipate()
            onSuccess()
        }
    }

    private fun askWantToParticipate() {
        DEBUG(
            this@RentDetailViewModel.name,
            "memberSize: ${members.value.size} max: ${rentModel.value.max} isWaiting ${
                rentModel.value.isWaiting(myUid)
            }"
        )

        if (members.value.size < rentModel.value.max && rentModel.value.isWaiting(myUid)) {
            val des = rentModel.value.waitings[myUid] ?: ""
            isAskWantToParticipate.value = AskWantToParticipate(true, des)
        }
    }

    fun initAskWantToParticipate() {
        isAskWantToParticipate.value = AskWantToParticipate(false)
    }

    fun removeParticipatedUser(isMember: Boolean, userModel: UserRentModel) = start {
        rentRepository.removeRentMember(rentModel.value.uid, userModel.uid, isMember)
        userRepository.removeUserReservations(myUid, rentModel.value.uid)
        if (isMember) {
            rentModel.value = rentModel.value.apply { member.remove(userModel.uid) }
            _members.value = members.value.toMutableList().apply { remove(userModel) }
        } else {
            rentModel.value = rentModel.value.apply { waitings.remove(userModel.uid) }
            _waiting.value = waiting.value.toMutableList().apply { remove(userModel) }
        }
        this.isMember.value = rentModel.value.isParticipated(myUid)
    }

    fun recruit() = start {
        // 모집 마감, 모짐 참가
        val closed = !rentModel.value.closed
        val updateRent = rentModel.value.copy(closed = closed)
        rentRepository.updateRentClosed(updateRent)
        rentModel.value = updateRent
    }

    fun recruit(des: String) {
        start {
            task {
                // 방장 제외
                if ((!checkIsBeforeTwoWeek() || rentModel.value.closed) && !isRoot) {
                    return@task
                }

                val userModel =
                    userRepository.getUserRentModel(myUid, des)?.copy(root = isRoot) ?: return@task

                userRepository.addUserReservations(
                    myUid,
                    rentModel.value.uid,
                    rentModel.value.getDateUpdateFormat()
                )
                rentRepository.addRentMember(
                    rentModel.value.uid,
                    myUid,
                    des,
                    rentModel.value.possibleMemberAdded()
                )
                if (rentModel.value.possibleMemberAdded()) {
                    rentModel.value = rentModel.value.apply { member[myUid] = des }
                    _members.value = members.value.toMutableList().apply { add(userModel) }
                } else {
                    rentModel.value = rentModel.value.apply { waitings[myUid] = des }
                    _waiting.value = waiting.value.toMutableList().apply { add(userModel) }
                }
                this.isMember.value = rentModel.value.isParticipated(myUid)
            }
        }
    }

    fun waitingToMember(des: String) {
        removeParticipatedUser(false, waiting.value.find { it.uid == myUid } ?: return)
        recruit(des)
    }

    fun deleteRent(onSuccess: () -> Unit) = start(onSuccess) {
        viewModelScope.launch {
            rentRepository.deleteRent(rentModel.value.uid)
        }
    }

    fun changeAccountOpened() {
        isAccountOpened.value = !isAccountOpened.value
    }

    fun checkIsBeforeTwoWeek() =
        rentModel.value.calendar.enableWeeklyParticipated(rentModel.value.isWeekly())

    fun getUser() =
        members.value.find { it.uid == myUid } ?: waiting.value.find { it.uid == myUid }

    private suspend fun task(
        uid: String = rentIdFromFCM ?: rentModel.value.uid,
        onStartTask: suspend () -> Unit
    ) {
        rentModel.value = rentRepository.getRent(uid) ?: return
        onStartTask()
    }

    data class AskWantToParticipate(
        val isAsk: Boolean,
        val des: String = ""
    )
}

