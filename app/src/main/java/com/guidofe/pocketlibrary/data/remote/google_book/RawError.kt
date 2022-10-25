package com.guidofe.pocketlibrary.data.remote.google_book

data class RawError(val code: Int, val message: String, val errors: List<RawErrorListElement>)
