package com.heejae.tenniverse.domain.model

import androidx.annotation.StringRes
import com.heejae.tenniverse.R

enum class GameType(val title: String, @StringRes val kor: Int) {
    MIXED("mixed", R.string.mixed),
    MEN_DOUBLE("menDouble", R.string.man_double),
    WOMEN_DOUBLE("womenDouble", R.string.women_double),
    MAN_SINGLE("manSingle", R.string.man_single),
    WOMAN_SINGLE("womanSingle", R.string.woman_single)
}