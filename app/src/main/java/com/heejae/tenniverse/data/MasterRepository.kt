package com.heejae.tenniverse.data

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.heejae.tenniverse.BuildConfig
import com.heejae.tenniverse.domain.model.UserModel
import com.heejae.tenniverse.util.FS_FIELD_REGISTERED
import com.heejae.tenniverse.util.USER_REGISTERED
import kotlinx.coroutines.tasks.await

class MasterRepository : BaseRepository() {
    private val newUsers = users.whereEqualTo(FS_FIELD_REGISTERED, false).orderBy("displayName")

    fun getUser() = Pager(config = PagingConfig(pageSize = UserPagingSource.PAGING_SIZE)) {
        UserPagingSource(users.orderBy("displayName").limit(UserPagingSource.PAGING_SIZE.toLong()))
    }.flow

    fun getNewUser() = Pager(config = PagingConfig(pageSize = UserPagingSource.PAGING_SIZE)) {
        UserPagingSource(newUsers.limit(UserPagingSource.PAGING_SIZE.toLong()))
    }.flow

    fun getUser(isRegistered: Boolean) =
        Pager(config = PagingConfig(pageSize = UserPagingSource.PAGING_SIZE)) {
            UserPagingSource(
                users.whereEqualTo(FS_FIELD_REGISTERED, isRegistered).orderBy("displayName")
                    .limit(UserPagingSource.PAGING_SIZE.toLong())
            )
        }.flow

    suspend fun updateUserRate(userModel: UserModel) {
        users.document(userModel.uid)
            .update("type", userModel.type.title).await()
    }

    suspend fun removeUser(userModel: UserModel) {
        users.document(userModel.uid).delete().await()
    }

    suspend fun acceptUser(userList: List<UserModel>) {
        userList.forEach {
            users.document(it.uid).update(mapOf(USER_REGISTERED to true)).await()
        }
    }

    suspend fun rejectUser(userList: List<UserModel>) {
        userList.forEach {
            removeUser(it)
        }
    }

    suspend fun checkPrevVersion(): Boolean {
        val data = versionRef.document("android").get().await()
        val version = data.data?.get("version") as String

        DEBUG(this@MasterRepository.name, "BuildConfig.VERSION_NAME ${BuildConfig.VERSION_NAME} version $version")
        return BuildConfig.VERSION_NAME < version
    }
}