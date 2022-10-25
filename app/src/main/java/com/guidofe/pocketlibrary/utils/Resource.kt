package com.guidofe.pocketlibrary.utils

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<Nothing>(message: String): Resource<Nothing>(message = message)
    class Loading<Nothing>(): Resource<Nothing>()

    fun isSuccess(): Boolean {
        return this is Success
    }
}
