package com.guidofe.pocketlibrary.model.repositories.google_book

import android.util.Log
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleBooksService {
    sealed class Error {
        class NoBookFound: Exception()
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl(GoogleBooksServiceEndpoints.baseUrl)
        .addConverterFactory(QueryFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    fun fetchVolumeByIsbn(isbn: String, onSuccessCallback: (ImportedBookData?) -> Unit, onFailureCallback: (code: Int) -> Unit) {
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        Log.d("test", "Hello")
        val call = service.getVolumesByQuery(QueryData(null, mapOf(QueryData.QueryKey.isbn to isbn)))
        Log.d("test", "Sweetie")
        call.enqueue(object: Callback<RawVolumeListResponse> {
            override fun onResponse(
                call: Call<RawVolumeListResponse>,
                response: Response<RawVolumeListResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("test", response.body().toString())
                    if (response.code() == 200) {
                        if (!response.body()?.items.isNullOrEmpty()) {
                            val rawBook = response.body()!!.items[0]
                            onSuccessCallback(rawBook.toImportedBookData())
                        }
                        else
                            onSuccessCallback(null)
                    } else {
                        Log.e("Test", call.request().url.toString())
                        Log.e("Test", "Something is null. " + response.body())
                        onFailureCallback(response.code())
                    }
                } else {
                    Log.e("Test", call.request().url.toString())
                    Log.e("Test", response.errorBody().toString())
                    onFailureCallback(response.code())
                }

            }

            override fun onFailure(call: Call<RawVolumeListResponse>, t: Throwable) {
                Log.d("test", "failed")
                throw t
            }

        })
    }
}