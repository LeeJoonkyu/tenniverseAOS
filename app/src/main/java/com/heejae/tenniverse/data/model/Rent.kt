package com.heejae.tenniverse.data.model

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.os.Parcelable
import com.heejae.tenniverse.domain.model.CourtType
import com.heejae.tenniverse.domain.model.DayOfWeek
import com.heejae.tenniverse.domain.model.DayTime
import com.heejae.tenniverse.domain.model.GameType
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.util.calendar.getTimeFormat
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Parcelize
data class Rent(
    val bank: String = "",
    val courtCount: Int = 0,
    val courtType: String = "",
    val date: String = "",
    val des: String = "",
    val fee: Int = 0,
    val gameType: String = "",
    val home: String? = null,
    val root: Boolean = false,
    val closed: Boolean = false,
    val location: String = "",
    val max: Int = 0,
    val member: HashMap<String, String> = hashMapOf(),
    val waitings : HashMap<String, String> = hashMapOf(),
    val ownerAccount: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerNumber: String = "",
    val time: Int = 0,
    val type: String = "",
): Parcelable {
    fun toModel(uid: String): RentModel {

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val date = dateFormat.parse(date) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val startHour = calendar.get(Calendar.HOUR_OF_DAY)

        val startAt = calendar.getTimeFormat()

        DEBUG(this.name, "date: $date dayOfWeek: $dayOfWeek")

        calendar.add(Calendar.HOUR, time)

        val endAt = calendar.getTimeFormat()

        val dayTime = if (startHour >= 12) DayTime.PM else DayTime.AM

        return RentModel(
            uid,
            bank,
            courtCount,
            calendar = Calendar.getInstance().apply { time = date },
            CourtType.values().find { it.title == courtType } ?: CourtType.CLAY,
            date =  this.date,
            year = year,
            month = month,
            day = day,
            dayOfWeek = DayOfWeek.values()[dayOfWeek - 1],
            dayTime = dayTime,
            startAt = startAt,
            endAt = endAt,
            des,
            fee,
            GameType.values().find { it.title == gameType } ?: GameType.MAN_SINGLE,
            home,
            root,
            closed,
            location,
            max,
            member,
            waitings,
            ownerAccount,
            ownerId,
            ownerName,
            ownerNumber,
            time,
            type = RentType.values().find { it.title == type } ?: RentType.DAILY
        )
    }
}

