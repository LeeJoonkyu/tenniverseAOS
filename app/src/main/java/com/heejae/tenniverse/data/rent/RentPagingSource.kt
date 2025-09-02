package com.heejae.tenniverse.data.rent

import android.lecture.myapplication.util.DEBUG
import android.lecture.myapplication.util.name
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heejae.tenniverse.data.model.Rent
import com.heejae.tenniverse.domain.model.RentModel
import com.heejae.tenniverse.util.calendar.checkPassed
import kotlinx.coroutines.tasks.await

class RentPagingSource(
    private val query: Query
) : PagingSource<QuerySnapshot, RentModel>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, RentModel>): QuerySnapshot? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, RentModel> {
        return kotlin.runCatching {

            val currentPage = params.key ?: query.get().await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage =
                query.startAfter(lastDocumentSnapshot).get().await()

            val data = currentPage.documents.mapNotNull {
                it.toObject<Rent>()?.toModel(it.id)
            }.filter {
                !it.calendar.checkPassed()
            }

            DEBUG(this@RentPagingSource.name, "size: ${data.size} data: $data")

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
        const val PAGING_SIZE = 10
    }
}