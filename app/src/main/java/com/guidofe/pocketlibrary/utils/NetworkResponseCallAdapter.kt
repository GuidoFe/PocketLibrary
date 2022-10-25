package com.guidofe.pocketlibrary.utils

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

class NetworkResponseCallAdapter<S: Any, E: Any>(
    private val successType: Type,
    private val errorBodyConverter: Converter<ResponseBody, E>
): CallAdapter<S, Call<NetworkResponse<S, E>>> {
    override fun responseType(): Type = successType
    override fun adapt(call: Call<S>): Call<NetworkResponse<S, E>> {
        Log.d("debug", "Url: ${call.request().url}")
        return NetworkResponseCall(call, errorBodyConverter)
    }

}