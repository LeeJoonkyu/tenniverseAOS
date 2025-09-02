package com.heejae.tenniverse.util.calendar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun String.dbDateToTime() = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).run {
    parse(this@dbDateToTime)?.time ?: -1L
}

fun String.dbDateToCalendar(): Calendar =
    SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).run {
        Calendar.getInstance().apply {
            timeInMillis = parse(this@dbDateToCalendar)?.time ?: -1L
        }
    }
