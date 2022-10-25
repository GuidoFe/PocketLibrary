package com.guidofe.pocketlibrary.data.remote.google_book

import com.guidofe.pocketlibrary.utils.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksServiceEndpoints {
    companion object {
        const val baseUrl = "https://www.googleapis.com/books/v1/"
    }
    @GET("volumes/{id}")
    suspend fun getVolume(@Path("id") id: Int): NetworkResponse<RawVolumeResponse, RawErrorResponse>

    @GET("volumes")
    suspend fun getVolumesByQuery(
        @Query("q") values: QueryData,
        @Query("maxResults") pageSize: Int = 40,
        @Query("startIndex") startIndex: Int = 0
    ): NetworkResponse<RawVolumeListResponse, RawErrorResponse>
}