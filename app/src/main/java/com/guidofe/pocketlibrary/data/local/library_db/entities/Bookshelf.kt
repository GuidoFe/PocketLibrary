package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize


@Entity(
    foreignKeys = [ForeignKey(
        entity = Room::class,
        parentColumns = arrayOf("roomId"),
        childColumns = arrayOf("parentRoom"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["parentRoom", "name"], unique = true)]
)
@Parcelize
data class Bookshelf(
    @PrimaryKey(autoGenerate = true) val bookshelfId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val parentRoom: Long
): Parcelable