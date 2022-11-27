package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class ProgressPhase { NOT_READ, SUSPENDED, IN_PROGRESS, READ, DNF }

@Entity(
    tableName = "progress",
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
data class Progress(
    @PrimaryKey val bookId: Long,
    @ColumnInfo var phase: ProgressPhase = ProgressPhase.IN_PROGRESS,
    @ColumnInfo var pagesRead: Int = 0,
    @ColumnInfo var trackPages: Boolean = false
) : Parcelable