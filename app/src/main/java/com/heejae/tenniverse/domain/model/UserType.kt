package com.heejae.tenniverse.domain.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.heejae.tenniverse.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class UserType(val title: String, @StringRes val kor: Int): Parcelable {
    CLUB("club", R.string.club),
    COURT("court", R.string.court),
    WEEKLY("weekly", R.string.weekly),
    NORMAL("normal", R.string.normal),
    NONE("none", R.string.none)
}