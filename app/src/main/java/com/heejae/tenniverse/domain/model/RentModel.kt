package com.heejae.tenniverse.domain.model

import android.os.Parcelable
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.util.calendar.updateFormat
import kotlinx.parcelize.Parcelize
import java.util.Calendar

@Parcelize
data class RentModel(
    val uid: String = "",
    val bank: String = "",
    val courtCount: Int = 0,
    val calendar: Calendar = Calendar.getInstance(),
    val courtType: CourtType = CourtType.CLAY,
    val date: String = "",
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val dayOfWeek: DayOfWeek = DayOfWeek.SUN,
    val dayTime: DayTime = DayTime.AM,
    val startAt: String = "",
    val endAt: String = "",
    val des: String = "",
    val fee: Int = 0,
    val gameType: GameType = GameType.MAN_SINGLE,
    val home: String? = null,
    val root: Boolean = false,
    val closed: Boolean = false,
    val location: String = "",
    val max: Int = 0,
    val member: HashMap<String, String> = hashMapOf(),
    val waitings: HashMap<String, String> = hashMapOf(),
    val ownerAccount: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerNumber: String = "",
    val time: Int = 0,
    val type: RentType = RentType.DAILY,
) : Parcelable {
    fun toRent() =
        Rent(
            bank, courtCount,
            courtType = courtType.title,
            date = getDateUpdateFormat(),
            des = des,
            fee = fee,
            gameType = gameType.title,
            home = home,
            root = root,
            closed = closed,
            location = location,
            max = max,
            member = member,
            waitings = waitings,
            ownerNumber = ownerNumber,
            ownerName = ownerName,
            ownerAccount = ownerAccount,
            ownerId = ownerId,
            time = time,
            type = type.title
        )

    fun isWeekly() = type == RentType.WEEKLY

    fun getUserType(userUid: String) = if (userUid == ownerId) {
        RentUserType.ROOT
    } else if (waitings.any { it.key == userUid }) {
        RentUserType.WAITING
    } else {
        RentUserType.STANDARD
    }

    fun isParticipated(userUid: String) = isMember(userUid) || isWaiting(userUid)

    fun possibleMemberAdded() = member.size < max

    fun isMember(userUid: String) =
        member.any { it.key == userUid }

    fun isWaiting(userUid: String) =
        waitings.any { it.key == userUid }

    fun getDateUpdateFormat() = calendar.updateFormat()
}