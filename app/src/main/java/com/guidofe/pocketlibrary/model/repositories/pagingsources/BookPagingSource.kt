package com.guidofe.pocketlibrary.model.repositories.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import javax.inject.Inject


class BookPagingSource @Inject constructor(val repo: LibraryRepository): PagingSource<Int, BookBundle>() {
    override fun getRefreshKey(state: PagingState<Int, BookBundle>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookBundle> {
        return try {
            val pageNumber = params.key ?: 0
            val response = repo.getBookBundles(pageNumber, params.loadSize)
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val nextKey = if (response.size == params.loadSize) pageNumber + 1 else null
            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("debug", e.toString())
            LoadResult.Error(e)
        }
    }

}