package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(foreignKeys = [ForeignKey(
    entity = Bookshelf::class,
    parentColumns = arrayOf("bookshelfId"),
    childColumns = arrayOf("parent_bookshelf"),
    onDelete = ForeignKey.CASCADE
)
])
@Parcelize
data class Shelf(
    @PrimaryKey(autoGenerate = true) val shelfId: Long,
    @ColumnInfo(name = "parent_bookshelf") val parentBookshelf: Long,
    @ColumnInfo val name: String,
): Parcelable