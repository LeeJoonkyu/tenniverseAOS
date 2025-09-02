package com.heejae.tenniverse.data.model

import com.heejae.tenniverse.domain.model.GenderType
import com.heejae.tenniverse.domain.model.OS
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.domain.model.UserRentModel
import com.heejae.tenniverse.domain.model.UserType

data class User(
    val career: Int = 0,
    val displayName: String = "",
    val fcmToken: String? = null,
    val gender: String = "",
    val phoneNumber: String = "",
    val profileUrl: String? = null,
    val registered: Boolean = false,
    val type: String = "none",
    val os: String? = "android",
    val reservations: HashMap<String, String> = hashMapOf()
) {
    fun toUserModel(uid: String) =
        UserModel(
            uid,
            career,
            displayName,
            fcmToken,
            GenderType.values().find { it.title == gender } ?: GenderType.MALE,
            phoneNumber,
            profileUrl,
            registered,
            UserType.values().find { it.title == type } ?: UserType.NONE,
            reservations,
            os = OS.values().find { it.title == os } ?: OS.ANDROID
        )

    fun toUserRentModel(uid: String, des: String) =
        UserRentModel(
            uid,
            career,
            displayName,
            fcmToken,
            GenderType.values().find { it.title == gender } ?: GenderType.MALE,
            phoneNumber,
            profileUrl,
            registered,
            UserType.values().find { it.title == type } ?: UserType.NONE,
            reservations = reservations,
            des = des
        )
}
