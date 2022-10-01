package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize


@Entity(
    foreignKeys = [ForeignKey(
        entity = Place::class,
        parentColumns = arrayOf("placeId"),
        childColumns = arrayOf("parentPlace"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("parentPlace")]
)
@Parcelize
data class Room(
    @PrimaryKey(autoGenerate = true) val roomId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val parentPlace: Long
): Parcelable