package com.heejae.tenniverse.persentation.home.rentdetail.comment

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.persentation.home.rentdetail.RentDetailViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_RENT
import com.heejae.tenniverse.util.PUT_EXTRA_RENT_MEMBER_COUNT
import com.heejae.tenniverse.util.calendar.enableWeeklyParticipated
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RentDetailCommentViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val initRentModel = savedStateHandle.get<RentModel>(PUT_EXTRA_RENT) ?: RentModel("")
    val memberCount = savedStateHandle.get<Int>(PUT_EXTRA_RENT_MEMBER_COUNT) ?: 0

    val rentModel = MutableStateFlow(initRentModel)
    private val userUid = auth.uid ?: ""
    val description = MutableStateFlow("")

    val isAccountOpened = MutableStateFlow(true)

    fun changeAccountOpened() {
        isAccountOpened.value = !isAccountOpened.value
    }

    fun recruit(onSuccess: () -> Unit) {
        start(onSuccess) {
            task {
                if (rentModel.value.closed || !checkIsBeforeTwoWeek()) {
                    return@task
                }
                rentRepository.addRentMember(
                    rentModel.value.uid,
                    userUid,
                    description.value,
                    rentModel.value.possibleMemberAdded()
                )
                userRepository.addUserReservations(
                    userUid,
                    rentModel.value.uid,
                    rentModel.value.getDateUpdateFormat()
                )
            }
        }
    }

    private fun checkIsBeforeTwoWeek() =
        rentModel.value.calendar.enableWeeklyParticipated(rentModel.value.isWeekly())

    private suspend fun task(onStartTask: suspend () -> Unit) {
        rentModel.value = rentRepository.getRent(rentModel.value.uid) ?: return
        DEBUG(this@RentDetailCommentViewModel.name, "rentModel: ${rentModel.value}")
        if (!rentModel.value.closed) {
            onStartTask()
        }
    }
}