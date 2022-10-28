package com.guidofe.pocketlibrary.utils

fun String?.nullIfEmptyOrBlank(): String? {
    if(this.isNullOrBlank())
        return null
    else
        return this
}

fun String.getInitials(): List<String> {
    return this.split(" ").filter{it.isNotBlank() && it.length > 1}.map{it[0].uppercase()}
}

fun String.areInitialsEqual(b: String): Boolean {
    return this.getInitials() == b.getInitials()
}