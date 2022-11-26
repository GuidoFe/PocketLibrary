package com.guidofe.pocketlibrary.model

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle

data class AppStats(
    val libraryBooksCount: Int,
    val currentlyBorrowedBooksCount: Int,
    val lentBooksCount: Int,
    val totalReadBooksCount: Int,
    val libraryBooksDnf: Int,
    val libraryBooksSuspended: Int,
    val libraryBooksCurrentlyReading: Int,
    val libraryBooksRead: Int,
    val booksCurrentlyReading: List<BookBundle>
) {
    val libraryBooksNotRead: Int
        get() = libraryBooksCount - libraryBooksSuspended - libraryBooksRead - libraryBooksDnf -
            libraryBooksCurrentlyReading
}