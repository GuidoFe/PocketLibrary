package com.guidofe.pocketlibrary.data.local.library_db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId"],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Place::class,
            parentColumns = arrayOf("placeId"),
            childColumns = arrayOf("placeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Room::class,
            parentColumns = arrayOf("roomId"),
            childColumns = arrayOf("roomId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Bookshelf::class,
            parentColumns = arrayOf("bookshelfId"),
            childColumns = arrayOf("bookshelfId"),
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [Index("bookId"), Index("placeId"), Index("roomId"), Index("bookshelfId")]
)
data class BookPlacement (
    val bookId: Long,
    val placeId: Long,
    val roomId: Long?,
    val bookshelfId: Long?
)
