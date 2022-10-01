package com.guidofe.pocketlibrary.model.repositories

import android.util.Log
import com.guidofe.pocketlibrary.data.remote.google_book.GoogleBooksServiceEndpoints
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.data.remote.google_book.QueryFactory
import com.guidofe.pocketlibrary.data.remote.google_book.RawVolumeListResponse
import com.guidofe.pocketlibrary.model.ImportedBookData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DefaultBookMetaRepository: BookMetaRepository {
    sealed class Error {
        class NoBookFound: Exception()
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl(GoogleBooksServiceEndpoints.baseUrl)
        .addConverterFactory(QueryFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //successCallback(null) if no book found, failureCallback(code) if other types of error
    override fun fetchVolumeByIsbn(isbn: String, onSuccessCallback: (ImportedBookData?) -> Unit, onFailureCallback: (code: Int, message: String) -> Unit) {
        //TODO: Manage book not found
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        val call = service.getVolumesByQuery(QueryData(null, mapOf(QueryData.QueryKey.isbn to isbn)))
        call.enqueue(object: Callback<RawVolumeListResponse> {
            override fun onResponse(
                call: Call<RawVolumeListResponse>,
                response: Response<RawVolumeListResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("debug", response.body().toString())
                    if (response.code() == 200) {
                        if (!response.body()?.items.isNullOrEmpty()) {
                            val rawBook = response.body()!!.items[0]
                            onSuccessCallback(rawBook.toImportedBookData())
                        }
                        else
                            onSuccessCallback(null)
                    } else {
                        Log.d("debug", call.request().url.toString())
                        Log.d("debug", "Something is null. " + response.body())
                        onFailureCallback(response.code(), response.message())
                    }
                } else {
                    Log.d("debug", call.request().url.toString())
                    Log.d("debug", response.errorBody().toString())
                    onFailureCallback(response.code(), response.message())
                }

            }

            override fun onFailure(call: Call<RawVolumeListResponse>, t: Throwable) {
                onFailureCallback(0, t.message ?: "Unknown error")
            }
        })
    }
}