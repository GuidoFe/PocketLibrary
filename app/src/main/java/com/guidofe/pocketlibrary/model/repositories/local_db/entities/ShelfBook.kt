package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId", "shelfId"],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Shelf::class,
            parentColumns = arrayOf("shelfId"),
            childColumns = arrayOf("shelfId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("shelfId")]
)
data class ShelfBook (
    val bookId: Long,
    val shelfId: Long
)