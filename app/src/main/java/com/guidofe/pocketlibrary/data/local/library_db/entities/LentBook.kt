package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "lent_book",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = arrayOf("bookId"),
            childColumns = arrayOf("bookId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LentBook(
    @PrimaryKey val bookId: Long,
    @ColumnInfo val who: String,
    @ColumnInfo val start: Date
) : Parcelable