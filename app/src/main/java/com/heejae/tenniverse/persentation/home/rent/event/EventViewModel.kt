package com.heejae.tenniverse.persentation.home.rent.event

import android.lecture.myapplication.util.DEBUG
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.CourtType
import com.heejae.tenniverse.domain.model.GameType
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.home.rent.common.RentCommonViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import com.heejae.tenniverse.util.calendar.updateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle,
) : RentCommonViewModel(auth, rentRepository, userRepository) {

    private val initUserModel = savedStateHandle.get<UserModel>(PUT_EXTRA_USER) ?: UserModel()

    init {
        initUserData()
    }
    private fun initUserData() {
        setName(initUserModel.displayName)
        setPhone(initUserModel.phoneNumber)
    }

    fun createRent(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            loading()
            if (!checkDate()) {
                onFailure()
                finished()
                return@launch
            }

            task {
                val date = calendar.value.updateFormat()
                val uid = auth.uid ?: return@task
                val rent = Rent(
                    bank = bank.value,
                    courtCount = 0,
                    courtType = CourtType.CLAY.title,
                    date = date,
                    des = des.value,
                    fee = 0,
                    gameType = GameType.MIXED.title,
                    location = place.value,
                    max = maxMember.value.toInt(),
                    member = hashMapOf<String, String>().apply {
                        put(uid, des.value)
                    },
                    waitings = hashMapOf(),
                    ownerId = uid,
                    ownerAccount = account.value,
                    ownerName = name.value,
                    ownerNumber = phone.value,
                    time = totalTime.value.toInt(),
                    type = RentType.EVENT.title
                ).also { DEBUG("RentViewModel", "rent: $it") }

                update(rent)
                onSuccess()
                finished()
            }
        }
    }
}