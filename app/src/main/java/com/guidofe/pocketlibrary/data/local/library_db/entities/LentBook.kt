package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Book::class,
    parentColumns = arrayOf("bookId"),
    childColumns = arrayOf("bookId"),
    onDelete = ForeignKey.CASCADE)]
)
data class LentBook(
    @PrimaryKey val bookId: Long,
    @ColumnInfo val who: String,
    @ColumnInfo val start: Date
): Parcelable