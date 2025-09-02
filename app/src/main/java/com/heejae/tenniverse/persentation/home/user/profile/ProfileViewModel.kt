package com.heejae.tenniverse.persentation.home.user.profile

import android.lecture.myapplication.util.DEBUG
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.domain.model.GenderType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.persentation.base.BaseViewModel
import com.heejae.tenniverse.util.PUT_EXTRA_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    var userModel = savedStateHandle.get<UserModel>(PUT_EXTRA_USER) ?: UserModel()

    private val _profileImg = MutableStateFlow(userModel.profileUrl)
    val profileImg = _profileImg.asStateFlow()

    val gender = MutableStateFlow(userModel.gender)

    val name = MutableStateFlow(userModel.displayName)

    val career = MutableStateFlow(userModel.career.toString())


    fun setProfileImg(uri: Uri) {
        _profileImg.value = uri.toString()
    }

    fun setGender(genderType: GenderType) {
        gender.value = genderType
    }

    fun updateUser(onSuccess: () -> Unit) {
        userModel = userModel.copy(
            profileUrl = profileImg.value,
            gender = gender.value,
            displayName = name.value,
            career = career.value.toInt()
        )

        val user = auth.currentUser ?: return

        DEBUG("ProfileViewModel", "uploadUser: ${user.uid}")

        viewModelScope.launch {
            loading()

            var downloadUri: Uri? = null

            if (profileImg.value != null) {
                DEBUG("ProfileViewModel", "profileImg: ${profileImg.value}")
                downloadUri = userRepository.uploadFirebaseStorageImg(
                    userModel.uid,
                    profileImg.value?.toUri() ?: return@launch
                )
            }
            userRepository.updateUserProfile(
                user.uid,
                downloadUri.toString(),
                name.value,
                gender.value,
                career.value.toInt()
            )
            finished()
            onSuccess()

        }
    }
}