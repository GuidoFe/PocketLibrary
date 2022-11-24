package com.guidofe.pocketlibrary.repositories.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.repositories.LocalRepository

class BorrowedBooksPagingSource(
    val repo: LocalRepository,
    val withReturned: Boolean
) : PagingSource<Int, BorrowedBundle>() {
    override fun getRefreshKey(state: PagingState<Int, BorrowedBundle>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, BorrowedBundle> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val response = repo.getBorrowedBundles(pageSize, pageNumber, withReturned)
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}