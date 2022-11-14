package com.guidofe.pocketlibrary.repositories

import android.util.Log
import com.guidofe.pocketlibrary.data.remote.google_book.GoogleBooksServiceEndpoints
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.data.remote.google_book.QueryFactory
import com.guidofe.pocketlibrary.data.remote.google_book.RawErrorResponse
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.utils.NetworkResponse
import com.guidofe.pocketlibrary.utils.NetworkResponseAdapterFactory
import com.guidofe.pocketlibrary.utils.Resource
import java.lang.Integer.min
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DefaultBookMetaRepository : BookMetaRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(GoogleBooksServiceEndpoints.baseUrl)
        .addConverterFactory(QueryFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .build()

    private fun <T> networkErrorResponseToResource(
        response: NetworkResponse<*, *>
    ): Resource.Error<T> {
        return when (response) {
            is NetworkResponse.Success ->
                Resource.Error("Trying to convert network Success to Resource Error")
            is NetworkResponse.NetworkError ->
                Resource.Error(response.error.message ?: "Network error")
            is NetworkResponse.ApiError ->
                Resource.Error(
                    "Api error ${response.code}: " +
                        (response.body as RawErrorResponse).error.message
                )
            is NetworkResponse.UnknownError ->
                Resource.Error("Unknown Error")
        }
    }
    private fun <T, E> networkResponseToResource(response: NetworkResponse<T, E>): Resource<T> {
        return if (response is NetworkResponse.Success)
            Resource.Success(response.value)
        else
            networkErrorResponseToResource(response)
    }
    // successCallback(null) if no book found, failureCallback(code) if other types of error
    override suspend fun fetchVolumesByIsbn(
        isbn: String,
        maxResults: Int
    ): Resource<List<ImportedBookData>> {
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        val response = service.getVolumesByQuery(
            QueryData(null, mapOf(QueryData.QueryKey.isbn to isbn)),
            pageSize = min(40, maxResults)
        )
        return if (response is NetworkResponse.Success) {
            Resource.Success(
                response.value.items?.mapNotNull { it.toImportedBookData() } ?: emptyList()
            )
        } else
            networkErrorResponseToResource(response)
    }

    override suspend fun searchVolumesByTitleOrAuthor(
        title: String?,
        author: String?,
        startIndex: Int,
        pageSize: Int
    ): Resource<List<ImportedBookData>> {
        if (title == null && author == null) {
            Log.d("debug", "title and author are null")
            return Resource.Success(emptyList())
        } else {
            val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
            val query: MutableMap<QueryData.QueryKey, String> = mutableMapOf()
            title?.let { query[QueryData.QueryKey.intitle] = it }
            author?.let { query[QueryData.QueryKey.inauthor] = it }
            val response = service.getVolumesByQuery(
                QueryData(
                    null,
                    query
                ),
                pageSize,
                startIndex
            )
            return if (response is NetworkResponse.Success) {
                Resource.Success(
                    response.value.items?.mapNotNull { it.toImportedBookData() } ?: emptyList()
                )
            } else
                networkErrorResponseToResource(response)
        }
    }

    override suspend fun searchVolumesByQuery(
        query: QueryData?,
        startIndex: Int,
        pageSize: Int
    ): Resource<List<ImportedBookData>> {
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        query?.let {
            val response = service.getVolumesByQuery(
                query,
                pageSize,
                startIndex
            )
            return if (response is NetworkResponse.Success) {
                Resource.Success(
                    response.value.items?.mapNotNull { it.toImportedBookData() } ?: emptyList()
                )
            } else
                networkErrorResponseToResource(response)
        } ?: return Resource.Success(emptyList())
    }
}