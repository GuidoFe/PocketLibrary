package com.guidofe.pocketlibrary.data.remote.google_book

data class QueryData (val text: String?, val parameters: Map<QueryKey, String>) {
    enum class QueryKey {inauthor, intitle, inpublisher, subject, isbn, lccn, oclc}
}