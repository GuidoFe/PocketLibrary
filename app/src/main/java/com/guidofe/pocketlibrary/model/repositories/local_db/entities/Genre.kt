package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class Genre(
    @PrimaryKey(autoGenerate = true) val genreId: Long,
    @ColumnInfo val name: String,
): Parcelable