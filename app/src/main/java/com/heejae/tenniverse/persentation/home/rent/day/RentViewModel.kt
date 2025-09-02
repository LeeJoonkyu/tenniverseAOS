package com.heejae.tenniverse.persentation.home.rent.day

import android.lecture.myapplication.util.DEBUG
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.rent.RentRepository
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.home.rent.common.RentCommonViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    auth: FirebaseAuth,
    rentRepository: RentRepository,
    userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
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

            // TODO : 유효성 검사 필요
            val rent = Rent(
                bank = bank.value,
                courtCount = countCourt.value.toInt(),
                courtType = courtType.value.title,
                date = uploadDate,
                des = des.value,
                fee = costCourt.value.toInt(),
                gameType = gameType.value.title,
                location = place.value,
                max = maxMember.value.toInt(),
                member = hashMapOf<String, String>().apply {
                    put(uid ?: return@launch, des.value)
                },
                waitings = hashMapOf(),
                ownerId = uid ?: return@launch,
                ownerAccount = account.value,
                ownerName = name.value,
                ownerNumber = phone.value,
                time = totalTime.value.toInt(),
                type = RentType.DAILY.title
            ).also { DEBUG("RentViewModel", "rent: $it") }

            update(rent)
            onSuccess()
            finished()
        }

    }
}