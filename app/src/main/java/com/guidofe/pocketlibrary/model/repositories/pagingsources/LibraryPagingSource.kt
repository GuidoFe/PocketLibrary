package com.guidofe.pocketlibrary.model.repositories.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem

class LibraryPagingSource(
    val repo: LibraryRepository
    ): PagingSource<Int, BookBundle>() {
    override fun getRefreshKey(state: PagingState<Int, BookBundle>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, BookBundle> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize
        val response = repo.getBookBundles(pageSize, pageNumber)
        val prevKey = if (pageNumber > 0) pageNumber - 1 else null
        val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
        return LoadResult.Page(
            data = response,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}