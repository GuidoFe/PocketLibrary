package com.guidofe.pocketlibrary.repositories.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository

class OnlineBooksPagingSource(
    val queryData: QueryData?,
    val repo: BookMetaRepository,
    val langRestrict: String? = null
) : PagingSource<Int, ImportedBookData>() {
    override fun getRefreshKey(state: PagingState<Int, ImportedBookData>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ImportedBookData> {
        // try {
        val pageNumber = params.key ?: 0
        val res = repo.searchVolumesByQuery(
            query = queryData,
            langRestrict = langRestrict,
            startIndex = pageNumber * params.loadSize,
            pageSize = params.loadSize
        )
        if (!res.isSuccess()) {
            Log.e(
                "debug",
                "OnlineBookPagingSource: Res is not success: " +
                    (res.message ?: "loading error")
            )
            return LoadResult.Error(Exception(res.message))
        }
        val response = res.data ?: emptyList()
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
        /*}
        catch (e: Exception) {
            Log.e("debug", "OnlineBookPagingSource: Failed loading: ${e.message}")
            return LoadResult.Error(e)
        }*/
    }
}