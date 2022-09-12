package com.guidofe.pocketlibrary.utils

fun String?.nullIfEmptyOrBlank(): String? {
    if(this.isNullOrBlank())
        return null
    else
        return this
}