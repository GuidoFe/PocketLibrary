package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.utils.NetworkResponse
import com.guidofe.pocketlibrary.utils.Resource

interface BookMetaRepository {
    suspend fun fetchVolumesByIsbn(isbn: String, maxResults: Int = 40): Resource<List<ImportedBookData>>
    suspend fun searchVolumesByTitleOrAuthor(
        title: String? = null,
        author: String? = null,
        startIndex: Int = 0,
        pageSize: Int = 40
    ): Resource<List<ImportedBookData>>

    suspend fun searchVolumesByQuery(
        query: QueryData?,
        startIndex: Int,
        pageSize: Int
    ): Resource<List<ImportedBookData>>
}