package com.guidofe.pocketlibrary.model

data class AppStats(
    val libraryBooksCount: Int,
    val currentlyBorrowedBooksCount: Int,
    val lentBooksCount: Int,
    val readBooksCount: Int,
)