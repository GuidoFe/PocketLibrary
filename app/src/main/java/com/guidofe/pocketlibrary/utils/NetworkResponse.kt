package com.guidofe.pocketlibrary.utils

import java.io.IOException

sealed class NetworkResponse<out T, out E> {
    data class Success<T>(val value: T) : NetworkResponse<T, Nothing>()
    data class ApiError<E>(val body: E, val code: Int) : NetworkResponse<Nothing, E>()
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing, Nothing>()
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing, Nothing>()
}