package com.guidofe.pocketlibrary.data.local.library_db.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookAuthor

@DatabaseView("SELECT * FROM bookauthor ORDER BY position")
data class SortedBookAuthor(
    @Embedded
    val bookAuthor: BookAuthor
)