package com.heejae.tenniverse.util.extension

import android.content.Context
import com.heejae.tenniverse.domain.model.UserType

fun String.getUserType(context: Context)
    = UserType.values().find { context.getString(it.kor) == this } ?: UserType.NONE