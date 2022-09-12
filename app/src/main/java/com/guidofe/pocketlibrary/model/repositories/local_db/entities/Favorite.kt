package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Book::class,
    parentColumns = arrayOf("bookId"),
    childColumns = arrayOf("bookId"),
    onDelete = ForeignKey.CASCADE)]
)
data class Favorite(
    @PrimaryKey val bookId: Long,
): Parcelable