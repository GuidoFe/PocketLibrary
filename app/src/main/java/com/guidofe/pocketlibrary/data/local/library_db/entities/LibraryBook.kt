package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class Progress { NOT_READ, SUSPENDED, IN_PROGRESS, READ }

@Entity(
    tableName = "library_book",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class LibraryBook(
    @PrimaryKey val bookId: Long,
    @ColumnInfo var isFavorite: Boolean = false,
    @ColumnInfo var progress: Progress = Progress.NOT_READ,
) : Parcelable