package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant
import kotlinx.parcelize.Parcelize

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
    @ColumnInfo val creation: Instant = Instant.now()
) : Parcelable