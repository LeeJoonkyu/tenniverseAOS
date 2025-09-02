package com.heejae.tenniverse.data.rent

import android.content.Context
import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.heejae.tenniverse.R
import com.heejae.tenniverse.data.BaseRepository
import com.heejae.tenniverse.data.model.DataModel
import com.heejae.tenniverse.data.model.FCMSendModel
import com.heejae.tenniverse.data.model.FCMSendModelIOS
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.data.model.User
import com.heejae.tenniverse.data.service.FCMApiService
import com.heejae.tenniverse.domain.model.OS
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.domain.model.RentType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.util.RENT_CLOSED
import com.heejae.tenniverse.util.RENT_DATE
import com.heejae.tenniverse.util.RENT_MEMBER
import com.heejae.tenniverse.util.RENT_TYPE
import com.heejae.tenniverse.util.RENT_WAITINGS
import com.heejae.tenniverse.util.USER_RESERVATIONS
import com.heejae.tenniverse.util.calendar.checkPassed
import com.heejae.tenniverse.util.calendar.dbDateToCalendar
import com.heejae.tenniverse.util.calendar.dbDateToTime
import com.heejae.tenniverse.util.calendar.updateFormat
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class RentRepository(
    private val fcmService: FCMApiService,
    private val context: Context,
) : BaseRepository() {

    private val myUid = FirebaseAuth.getInstance().uid

    private val query = rents
        .orderBy(RENT_DATE)
        .limit(RentPagingSource.PAGING_SIZE.toLong())

    suspend fun pushFCM(title: String, token: List<Pair<String, String>>, uid: String) {
        token.forEach {
            if (it.second == OS.ANDROID.title) {
                fcmService.sendAskWantToParticipate(
                    FCMSendModel(
                        it.first,
                        DataModel(
                            context.getString(R.string.notification_title, title),
                            context.getString(R.string.notification_body),
                            uid
                        )
                    )
                )
            } else {
                val iosSendModel = FCMSendModelIOS(
                    it.first,
                    FCMSendModelIOS.NotificationModel(
                        context.getString(R.string.notification_title, title),
                        context.getString(R.string.notification_body),
                        uid
                    ),
                    DataModel(
                        context.getString(R.string.notification_title, title),
                        context.getString(R.string.notification_body),
                        uid
                    )
                )

                DEBUG(this@RentRepository.name, "iosSendModel: $iosSendModel")

                fcmService.sendAskWantToParticipateIOS(iosSendModel)
            }
        }
    }

    suspend fun uploadRent(rent: Rent) =
        rents.add(rent).await().id

    fun getRents(type: RentType) =
        Pager(config = PagingConfig(pageSize = RentPagingSource.PAGING_SIZE)) {
            RentPagingSource(
                if (type == RentType.ALL) {
                    query
                } else {
                    rents.whereEqualTo(RENT_TYPE, type.title)
                        .orderBy(RENT_TYPE)
                        .orderBy(RENT_DATE)
                        .limit(RentPagingSource.PAGING_SIZE.toLong())
                }
            )
        }.flow

    suspend fun getRent(uid: String) =
        rents.document(uid).get().await().toObject<Rent>()?.toModel(uid)

    suspend fun getRents(rentList: List<String>) = rentList.mapNotNull {
        rents.document(it).get().await().toObject<Rent>()?.toModel(it) ?: kotlin.run {
            deleteDummyUsers(it)
            null
        }
    }.filter {
        !it.calendar.checkPassed()
    }.filter {
        it.member[myUid] != null
    }.sortedBy {
        it.date
    }

    private suspend fun deleteDummyUsers(rentUid: String) {
        users.document(myUid ?: return).update(
            mapOf(
                "$USER_RESERVATIONS.$rentUid" to FieldValue.delete()
            )
        ).await()
    }

    suspend fun getRentOfUsers(
        userUid: String?,
        reservations: HashMap<String, String>
    ): List<RentModel> {

        val list = mutableListOf<RentModel>()

        val currentTime = System.currentTimeMillis()
        ArrayList(reservations.entries)
            .sortedBy { it.value }
            .filter {
                it.value.dbDateToTime() > currentTime
            }
            .forEach { (uid, _) ->
                val rent = getRent(uid) ?: return@forEach
                if (rent.member[userUid] != null) {
                    list.add(rent)
                }
            }

        return list
    }

    suspend fun createWeeklyRent(
        memberList: List<UserModel>,
        rent: Rent,
        cnt: Int
    ): HashMap<String, String> {

        val calendar = rent.date.dbDateToCalendar()
        val members = hashMapOf<String, String>()
        val rentList = hashMapOf<String, String>()
        val date = calendar.updateFormat()
        memberList.forEach { members[it.uid] = context.getString(R.string.default_description) }

        val rootRentUid =
            rents.add(rent.copy(member = members, root = true, date = date))
                .await().id
        val childRent = rent.copy(home = rootRentUid, member = members)

        rentList[rootRentUid] = date

        repeat(cnt - 1) {
            calendar.add(Calendar.DAY_OF_MONTH, 7)
            val date = calendar.updateFormat()
            val uid = rents.add(childRent.copy(date = date)).await().id
            rentList[uid] = date
        }

        return rentList
    }

    suspend fun updateRentClosed(rentModel: RentModel) {
        rents.document(rentModel.uid).update(RENT_CLOSED, rentModel.closed).await()
    }

    suspend fun removeRentMember(rentUid: String, uid: String, isMember: Boolean) {
        rents.document(rentUid).update(
            mapOf(
                if (isMember) {
                    "$RENT_MEMBER.$uid" to FieldValue.delete()
                } else {
                    "$RENT_WAITINGS.$uid" to FieldValue.delete()
                }
            )
        ).await()

        if (isMember) {
            isClosed(rentUid) { model ->
                pushFCM(
                    model.location,
                    model.waitings.mapNotNull {
                        users.document(it.key).get().await().toObject<User>()?.let { user ->
                            Pair(user.fcmToken ?: return@mapNotNull null, user.os ?: return@mapNotNull null)
                        }
                    },
                    rentUid
                )
            }
        }
    }

    suspend fun addRentMember(rentUid: String, uid: String, des: String, isMember: Boolean) {
        rents.document(rentUid).update(
            mapOf(
                if (isMember) {
                    "$RENT_MEMBER.$uid" to des
                } else {
                    "$RENT_WAITINGS.$uid" to des
                }
            )
        ).await()
    }

    suspend fun deleteRent(uid: String) {
        rents.document(uid).delete().await()
    }

    private suspend fun isClosed(uid: String, task: suspend (RentModel) -> Unit) {
        val rent = getRent(uid)
        if (rent != null) {
            if (!rent.closed) {
                task(rent)
            }
        }
    }
}