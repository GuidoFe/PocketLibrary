package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Place(
    @PrimaryKey(autoGenerate = true) val placeId: Long,
    @ColumnInfo val name: String,
): Parcelable