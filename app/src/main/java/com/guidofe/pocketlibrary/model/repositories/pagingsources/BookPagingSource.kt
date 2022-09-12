package com.guidofe.pocketlibrary.model.repositories.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guidofe.pocketlibrary.model.repositories.local_db.AppDatabase
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle
import com.guidofe.pocketlibrary.model.repositories.local_db.daos.BookBundleDao
import javax.inject.Inject


class BookPagingSource @Inject constructor(val dao: BookBundleDao): PagingSource<Int, BookBundle>() {
    override fun getRefreshKey(state: PagingState<Int, BookBundle>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookBundle> {
        TODO("Not yet implemented")
    }

}