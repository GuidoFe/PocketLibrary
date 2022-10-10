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

    private fun fetchListOfBooks(
        call: Call<RawVolumeListResponse>,
        onSuccessCallback: (List<ImportedBookData>) -> Unit,
        onFailureCallback: (code: Int, message: String) -> Unit
    ) {
        call.enqueue(object: Callback<RawVolumeListResponse> {
            override fun onResponse(
                call: Call<RawVolumeListResponse>,
                response: Response<RawVolumeListResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        if (!response.body()?.items.isNullOrEmpty()) {
                            onSuccessCallback(response.body()!!.items.map{it.toImportedBookData()})
                        }
                        else
                            onSuccessCallback(listOf())
                    } else {
                        onFailureCallback(response.code(), response.message())
                    }
                } else {
                    onFailureCallback(response.code(), response.message())
                }

            }

            override fun onFailure(call: Call<RawVolumeListResponse>, t: Throwable) {
                onFailureCallback(0, t.message ?: "Unknown error")
            }
        })
    }
    //successCallback(null) if no book found, failureCallback(code) if other types of error
    override fun fetchVolumesByIsbn(isbn: String, onSuccessCallback: (List<ImportedBookData>) -> Unit, onFailureCallback: (code: Int, message: String) -> Unit) {
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        val call = service.getVolumesByQuery(QueryData(null, mapOf(QueryData.QueryKey.isbn to isbn)))
        fetchListOfBooks(call, onSuccessCallback, onFailureCallback)
    }

    override fun searchVolumesByTitle(
        title: String,
        onSuccessCallback: (List<ImportedBookData>) -> Unit,
        onFailureCallback: (code: Int, message: String) -> Unit
    ) {
        val service = retrofit.create(GoogleBooksServiceEndpoints::class.java)
        val call = service.getVolumesByQuery(QueryData(null, mapOf(QueryData.QueryKey.intitle to title)))
        fetchListOfBooks(call, onSuccessCallback, onFailureCallback)
    }
}