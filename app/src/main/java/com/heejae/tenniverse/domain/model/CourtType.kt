package com.heejae.tenniverse.domain.model

import androidx.annotation.StringRes
import com.heejae.tenniverse.R

enum class CourtType(val title: String, @StringRes val kor: Int) {
    HARD("hard", R.string.hard),
    CLAY("clay", R.string.clay),
    GRASS("grass", R.string.grass)
}