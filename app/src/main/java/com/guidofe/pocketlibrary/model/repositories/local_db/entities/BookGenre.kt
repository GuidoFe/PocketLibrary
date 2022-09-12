package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Genre::class,
            parentColumns = arrayOf("genreId"),
            childColumns = arrayOf("genreId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("genreId")]
)
data class BookGenre (
    val bookId: Long,
    val genreId: Long
)