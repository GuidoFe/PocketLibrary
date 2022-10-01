package com.guidofe.pocketlibrary.data.local.library_db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId", "authorId"],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Author::class,
            parentColumns = arrayOf("authorId"),
            childColumns = arrayOf("authorId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("authorId")]
)
data class BookAuthor (
    val bookId: Long,
    val authorId: Long
)