package com.heejae.tenniverse.data

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.heejae.tenniverse.data.model.User
import com.heejae.tenniverse.domain.model.GenderType
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.domain.model.UserRentModel
import com.heejae.tenniverse.util.RENT_MEMBER
import com.heejae.tenniverse.util.RENT_WAITINGS
import com.heejae.tenniverse.util.USER_CAREER
import com.heejae.tenniverse.util.USER_DISPLAY_NAME
import com.heejae.tenniverse.util.USER_FCM_TOKEN
import com.heejae.tenniverse.util.USER_GENDER
import com.heejae.tenniverse.util.USER_OS
import com.heejae.tenniverse.util.USER_PROFILE_URL
import com.heejae.tenniverse.util.USER_RESERVATIONS
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
) : BaseRepository() {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    suspend fun getMembers(uid: String, members: HashMap<String, String>) =
        members.mapNotNull {
            getUserRentModel(it.key, it.value) ?: kotlin.run {
                rents.document(uid).update(
                    mapOf(
                        "$RENT_MEMBER.${it.key}" to FieldValue.delete()
                    )
                ).await()
                null
            }
        }

    suspend fun getWaiting(uid: String, waiting: HashMap<String, String>) =
        waiting.mapNotNull {
            getUserRentModel(it.key, it.value) ?: kotlin.run {
                deleteDummyRents(uid, it.key)
                null
            }
        }

    private suspend fun deleteDummyRents(userUid: String, rentUid: String) {
        rents.document(userUid).update(
            mapOf(
                "$RENT_WAITINGS.$rentUid" to FieldValue.delete()
            )
        ).await()
    }

    suspend fun getUserRentModel(uid: String, des: String): UserRentModel? {
        return kotlin.runCatching {
            users.document(uid).get().await().toObject<User>()?.toUserRentModel(uid, des)
        }.getOrElse {
            null
        }
    }

    suspend fun updateUserToken() {
        val uid = auth.uid ?: return
        val token = FirebaseMessaging.getInstance().token.await()

        val map = mapOf(
            USER_FCM_TOKEN to token,
            USER_OS to "android",
        )
        users.document(uid).update(map).await()
    }

    suspend fun updateUserProfile(
        uid: String,
        profileUrl: String?,
        name: String,
        gender: GenderType,
        career: Int
    ) {
        val map = mapOf(
            USER_DISPLAY_NAME to name,
            USER_GENDER to gender.title,
            USER_CAREER to career,
            USER_PROFILE_URL to profileUrl
        )
        users.document(uid).update(map).await()
    }

    suspend fun currentUser(): UserModel? {
        val uid = auth.uid ?: return null
        DEBUG(this@UserRepository.name, "uid $uid")
        return users.document(uid).get().await().toObject<User>()?.toUserModel(uid)
    }

    suspend fun checkUser(uid: String) =
        users.document(uid).get().await().data != null

    suspend fun updateUserReservations(uid: String, reservations: HashMap<String, String>) {
        users.document(uid).update(USER_RESERVATIONS, reservations).await()
    }


    suspend fun removeUserReservations(userUid: String, uid: String) {
        users.document(userUid).update(
            mapOf(
                "$USER_RESERVATIONS.$uid" to FieldValue.delete()
            )
        ).await()
    }

    suspend fun addUserReservations(userUid: String, uid: String, date: String) {
        users.document(userUid).update(
            mapOf(
                "$USER_RESERVATIONS.$uid" to date
            )
        ).await()
    }

    suspend fun updateWeeklyRentUsers(
        userList: List<UserModel>,
        reservations: HashMap<String, String>
    ) {
        userList.forEach {
            users.document(it.uid)
                .update(USER_RESERVATIONS, it.reservations.apply {
                    putAll(reservations)
                }).await()
        }
    }

    suspend fun uploadUser(user: FirebaseUser, userModel: User, uri: Uri?) {
        DEBUG(this@UserRepository.name, "uploadUser: ${user.uid} userModel: $userModel uri: $uri")
        var downloadUri : Uri? = null
        if (uri != null) {
            downloadUri = uploadFirebaseStorageImg(user.uid, uri)
        }
        uploadFirebaseUser(user, userModel, downloadUri)
    }

    suspend fun uploadFirebaseStorageImg(uid: String, uri: Uri): Uri {
        val uploadTask = storageRef.child("userImage/$uid/image.jpeg")

        return uploadTask.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception
            }
            uploadTask.downloadUrl
        }.await()
    }

    private suspend fun uploadFirebaseUser(user: FirebaseUser, userModel: User, uri: Uri?) {
        DEBUG(
            "MainViewModel",
            "user $user uri: $uri"
        )
        users.document(user.uid)
            .set(userModel.copy(profileUrl = uri.toString())).await()
    }
}
