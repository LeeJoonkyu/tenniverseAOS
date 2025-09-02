package com.heejae.tenniverse.domain.model

import android.os.Parcelable
import com.heejae.tenniverse.data.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val uid: String = "",
    val career: Int = 0,
    val displayName: String = "",
    val fcmToken: String? = null,
    val gender: GenderType = GenderType.MALE,
    val phoneNumber: String = "",
    val profileUrl: String? = null,
    val registered: Boolean = false,
    val type: UserType = UserType.NONE,
    val reservations: HashMap<String, String> = hashMapOf(),
    val os: OS = OS.ANDROID,
): Parcelable {
    fun toUser()
        = User(
            career, displayName, fcmToken,
            gender = gender.title,
            phoneNumber = phoneNumber,
            profileUrl = profileUrl,
            registered = registered,
            type = type.title,
            reservations = reservations,
        )

    fun checkRegularUser() =
        type == UserType.CLUB || type == UserType.COURT
}