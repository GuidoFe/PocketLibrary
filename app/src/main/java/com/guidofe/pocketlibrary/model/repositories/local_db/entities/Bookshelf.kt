package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(foreignKeys = [ForeignKey(
    entity = Room::class,
    parentColumns = arrayOf("roomId"),
    childColumns = arrayOf("parent_room"),
    onDelete = ForeignKey.CASCADE
)
])
@Parcelize
data class Bookshelf(
    @PrimaryKey(autoGenerate = true) val bookshelfId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "parent_room") val parentRoom: Long
): Parcelable