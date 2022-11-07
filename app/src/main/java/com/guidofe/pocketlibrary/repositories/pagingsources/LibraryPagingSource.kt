package com.guidofe.pocketlibrary.repositories.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.repositories.LocalRepository

class LibraryPagingSource(
    val repo: LocalRepository
) : PagingSource<Int, LibraryBundle>() {
    override fun getRefreshKey(state: PagingState<Int, LibraryBundle>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, LibraryBundle> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val response = repo.getLibraryBundles(pageSize, pageNumber)
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}