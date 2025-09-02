package com.heejae.tenniverse.data

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heejae.tenniverse.data.model.User
import com.heejae.tenniverse.domain.model.UserModel
import kotlinx.coroutines.tasks.await

class UserPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, UserModel>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, UserModel>): QuerySnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UserModel> {
        return kotlin.runCatching {

            val currentPage = params.key ?: query.get().await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage =
                query.startAfter(lastDocumentSnapshot).get().await()

            for (document in currentPage.documents) {
                DEBUG(this@UserPagingSource.name, "document: $document")
            }

            val data = currentPage.documents.mapNotNull {
                it.toObject<User>()?.toUserModel(it.id)
            }

            DEBUG(this@UserPagingSource.name, "size: ${data.size} data: $data")

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextPage
            )

        }.getOrElse {
            LoadResult.Error(it)
        }

    }

    companion object {
        const val PAGING_SIZE = 15
    }
}