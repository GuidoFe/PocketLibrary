package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(indices = [Index("name", unique = false)])
@Parcelize
data class Genre(
    @PrimaryKey(autoGenerate = true) val genreId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo(name = "english_name") val englishName: String?,
    @ColumnInfo val lang: String,
) : Parcelable