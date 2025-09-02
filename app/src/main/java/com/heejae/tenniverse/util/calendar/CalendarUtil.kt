package com.heejae.tenniverse.util.calendar

import android.lecture.myapplication.util.DEBUG
import com.heejae.tenniverse.domain.model.DayTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun Calendar.year() = get(Calendar.YEAR)
fun Calendar.month() = get(Calendar.MONTH) + 1
fun Calendar.day() = get(Calendar.DAY_OF_MONTH)

fun Calendar.hour() = get(Calendar.HOUR_OF_DAY)

fun Calendar.minute() = get(Calendar.MINUTE)

fun Calendar.dayTime() =
    if (hour() >= 12) DayTime.PM else DayTime.AM

fun Calendar.printCalendar() {
    DEBUG(
        "Calendar",
        "year: ${year()} month: ${month()} day: ${day()} hour: ${hour()} minute: ${minute()}"
    )
}

fun Calendar.checkPassed(): Boolean {
    // 현재 시간이 기존 시간 보다 작아야 함.
    val currentCalendar = Calendar.getInstance(Locale.getDefault())

    // 2024 01 01
    // 2023 12 26 current

    if (year() < currentCalendar.year()) return true
    if (year() == currentCalendar.year() && month() < currentCalendar.month()) return true

    return if (timeInMillis < currentCalendar.timeInMillis) { // 현재 시간 보다 작을 경우
        day() != currentCalendar.day()
    }else {
        false
    }
}

fun Calendar.enableWeeklyParticipated(isWeekly: Boolean): Boolean {
    // 모든 정기대관 코트는 일주일전, AM:10:00에 신청 가능해야합니다. 예)12/4일 예약분은 11/27일 AM:10:00부터 예약 가능
    val currentCalendar = Calendar.getInstance(Locale.getDefault())

    if (timeInMillis <= currentCalendar.timeInMillis) return false

    if (!isWeekly) return true

    val tmpCalendar = Calendar.getInstance()
    tmpCalendar.timeInMillis = timeInMillis

    tmpCalendar.apply {
        add(Calendar.DAY_OF_MONTH, -7)
        set(Calendar.HOUR_OF_DAY, 10)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    if (currentCalendar.timeInMillis >= tmpCalendar.timeInMillis) return true

    return false
}

fun Calendar.getDateFormat(): String {
    val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
    return dateFormat.format(time)
}

fun Calendar.getTimeFormat(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val tmpCalendar = Calendar.getInstance()

    tmpCalendar.timeInMillis = timeInMillis
    if (hour() > 12) {
        tmpCalendar.add(Calendar.HOUR, -12)
    }

    return dateFormat.format(tmpCalendar.time)
}

fun Calendar.updateFormat(): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return formatter.format(time)
}

fun Calendar.rentHourFormat() : String {
    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    return formatter.format(time)
}