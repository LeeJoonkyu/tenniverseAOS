package com.heejae.tenniverse.util

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.inSpans
import com.heejae.tenniverse.R


fun SpannableStringBuilder.append8(
    context: Context,
    font: Int = R.font.sc_dream_8,
    color: Int = R.color.black,
    text: String
) =
    inSpans(
        StyleSpan(ResourcesCompat.getFont(context, font)?.style ?: 0),
        ForegroundColorSpan(ContextCompat.getColor(context, color))
    ) {
        append(text)
    }