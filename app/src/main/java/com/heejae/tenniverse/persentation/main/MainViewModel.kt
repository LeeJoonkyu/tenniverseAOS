package com.heejae.tenniverse.persentation.main

import android.lecture.myapplication.util.DEBUG
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.heejae.tenniverse.data.UserRepository
import com.heejae.tenniverse.data.model.User
import com.heejae.tenniverse.domain.model.CredentialState
import com.heejae.tenniverse.persentation.login.model.PhoneCodeModel
import com.heejae.tenniverse.domain.model.RegisterState
import com.heejae.tenniverse.persentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val phoneNumber = MutableStateFlow("")
    val phoneFormatting
        get() = "+82${
            phoneNumber.value.apply {
                removeRange(1, length - 1)
            }
        }"

    var phoneAuthCredential: PhoneAuthCredential? = null

    private val _phoneNumberValidation = MutableStateFlow(false)
    val phoneNumberValidation = _phoneNumberValidation.asStateFlow()

    val certificationNumber = MutableStateFlow("")
    private val _certificationNumberValidation = MutableStateFlow(false)
    val certificationNumberValidation = _certificationNumberValidation.asStateFlow()

    private val _signInValidation = MutableStateFlow(false)
    val signInValidation = _signInValidation.asStateFlow()

    val name = MutableStateFlow("")

    private val _nameValidation = MutableStateFlow(false)
    val nameValidation = _nameValidation.asStateFlow()

    private val _gender = MutableStateFlow(Gender.NOTHING)
    val gender = _gender.asStateFlow()

    private val _genderValidation = MutableStateFlow(false)
    val genderValidation = _genderValidation.asStateFlow()

    val career = MutableStateFlow("")

    private val _careerValidation = MutableStateFlow(false)
    val careerValidation = _careerValidation.asStateFlow()

    private val _profileImg = MutableStateFlow<String?>(null)
    val profileImg = _profileImg.asStateFlow()

    private val _phoneCodeModel = MutableStateFlow<PhoneCodeModel?>(null)
    val phoneCodeModel = _phoneCodeModel.asStateFlow()

    private val _credentialState = MutableStateFlow<CredentialState>(CredentialState.UnInitialized)
    val credentialState = _credentialState.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.UnInitialized)
    val registerState = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun setUser(user: FirebaseUser) {
        _currentUser.value = user
        _signInValidation.value = true
    }

    fun setCodeModel(phoneCodeModel: PhoneCodeModel) {
        _phoneCodeModel.value = phoneCodeModel
    }

    fun setUserState(state: CredentialState) {
        _credentialState.value = state
    }

    fun setProfileImg(uri: Uri) {
        _profileImg.value = uri.toString()
    }

    fun setCertificationNumber(number: String) {
        certificationNumber.value = number
    }

    fun uploadUser() {
        DEBUG(
            "MainViewModel",
            "uploadUser career: ${career.value} name: ${name.value} gender ${gender.value.en}"
        )
        val user = User(
            career = career.value.toInt(),
            displayName = name.value,
            gender = gender.value.en,
            phoneNumber = phoneNumber.value,
            registered = false
        )

        DEBUG(
            "MainViewModel",
            "user: $user"
        )

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            userRepository.uploadUser(
                currentUser.value ?: return@launch,
                user,
                profileImg.value?.toUri()
            )

            _registerState.value = RegisterState.Success
        }
    }

    fun validatePhoneNumber() {
        _phoneNumberValidation.value = phoneNumber.value.length == PHONE_NUMBER_SIZE
    }

    fun validateCertificationNumber() {
        _certificationNumberValidation.value =
            certificationNumber.value.length == CERTIFICATION_NUMBER_SIZE
    }

    fun validateName() {
        _nameValidation.value = name.value.isNotEmpty()
    }

    fun chooseGender(gender: Gender) {
        _gender.value = gender
        _genderValidation.value = true
    }

    fun validateCareer() {
        _careerValidation.value = career.value.isNotEmpty()
    }

    fun clearCertificationNumber() {
        certificationNumber.value = ""
    }

    fun checkRegisteredUser(user: FirebaseUser) {
        viewModelScope.launch {
            val currentUser = userRepository.currentUser()
            setUserState(
                CredentialState.Success.SignInCredential(
                    user,
                    currentUser != null,
                    currentUser?.registered == true
                )
            )
        }
    }

    companion object {
        const val CERTIFICATION_NUMBER_SIZE = 6
        const val PHONE_NUMBER_SIZE = 11
    }

    enum class Gender(val kor: String, val en: String) {
        MAN("남", "male"), WOMAN("여", "female"), NOTHING("", "")
    }
}