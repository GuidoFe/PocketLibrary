package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "borrowed_book",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BorrowedBook(
    @PrimaryKey val bookId: Long,
    @ColumnInfo val who: String? = null,
    @ColumnInfo val start: Instant = Instant.now(),
    @ColumnInfo val end: LocalDate? = null,
    @ColumnInfo val isReturned: Boolean = false,
    @ColumnInfo(name = "notification_time") val notificationTime: Instant?
) : Parcelable