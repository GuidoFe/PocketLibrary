package com.guidofe.pocketlibrary.data.remote.google_book

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksServiceEndpoints {
    companion object {
        const val baseUrl = "https://www.googleapis.com/books/v1/"
    }
    @GET("volumes/{id}")
    fun getVolume(@Path("id") id: Int) : Call<RawVolumeResponse>

    @GET("volumes")
    fun getVolumesByQuery(@Query("q") values: QueryData) : Call<RawVolumeListResponse>
}