package com.heejae.tenniverse.domain.model

import androidx.annotation.StringRes
import com.heejae.tenniverse.R

enum class GenderType(val title: String, @StringRes val kor: Int) {
    MALE("male", R.string.man_profile),
    FEMALE("female", R.string.woman_profile)
}