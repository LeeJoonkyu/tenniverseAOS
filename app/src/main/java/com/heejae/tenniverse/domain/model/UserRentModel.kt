package com.heejae.tenniverse.domain.model


data class UserRentModel(
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
    val des: String = "",
    val root: Boolean = false,
)