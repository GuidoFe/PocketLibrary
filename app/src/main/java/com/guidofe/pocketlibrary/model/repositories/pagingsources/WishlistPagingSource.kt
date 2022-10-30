package com.guidofe.pocketlibrary.model.repositories.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.model.repositories.LocalRepository

class WishlistPagingSource(
    val repo: LocalRepository
    ): PagingSource<Int, WishlistBundle>() {
    override fun getRefreshKey(state: PagingState<Int, WishlistBundle>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, WishlistBundle> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val response = repo.getWishlistBundles(pageSize, pageNumber)
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}