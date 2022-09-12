package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(foreignKeys = [ForeignKey(
    entity = Place::class,
    parentColumns = arrayOf("placeId"),
    childColumns = arrayOf("parent_place"),
    onDelete = ForeignKey.CASCADE
)
])
@Parcelize
data class Room(
    @PrimaryKey(autoGenerate = true) val roomId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "parent_place") val parentPlace: Long
): Parcelable