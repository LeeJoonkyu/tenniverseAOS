package com.heejae.tenniverse.domain.model

import androidx.annotation.StringRes
import com.heejae.tenniverse.R

enum class RentType(val title: String, @StringRes val kor: Int) {
    ALL("", 0),
    DAILY("daily", R.string.rental_daily),
    WEEKLY("weekly", R.string.rental_regular),
    EVENT("event", R.string.event)
}