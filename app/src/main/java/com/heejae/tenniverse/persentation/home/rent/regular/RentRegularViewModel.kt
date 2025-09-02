package com.heejae.tenniverse.persentation.home.rent.regular

import android.lecture.myapplication.util.DEBUG
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.persentation.home.rent.common.RentCommonViewModel
import com.heejae.tenniverse.util.calendar.updateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RentRegularViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val rentRepository: RentRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : RentCommonViewModel(auth, rentRepository, userRepository) {

    val createCount = MutableStateFlow("")

    fun createRent(onSuccess: (Rent, Int) -> Unit, onFailure: () -> Unit) {
        if (!checkDate()) {
            onFailure()
            return
        }

        // TODO : 유효성 검사 필요
        val rent = Rent(
            bank = bank.value,
            courtCount = countCourt.value.toInt(),
            courtType = courtType.value.title,
            date = calendar.value.updateFormat(),
            des = des.value,
            fee = costCourt.value.toInt(),
            gameType = gameType.value.title,
            location = place.value,
            max = maxMember.value.toInt(),
            member = hashMapOf(),
            waitings = hashMapOf(),
            ownerId = auth.uid ?: return,
            ownerAccount = account.value,
            ownerName = userModel.value.displayName,
            ownerNumber = userModel.value.phoneNumber,
            time = totalTime.value.toInt(),
            type = RentType.WEEKLY.title
        ).also { DEBUG("RentViewModel", "rent: $it") }

        onSuccess(
            rent,
            createCount.value.toInt()
        )
    }
}