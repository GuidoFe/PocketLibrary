package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class Media {BOOK, EBOOK}

@Entity
@Parcelize
data class Book (
    @PrimaryKey(autoGenerate = true) val bookId: Long,
    @ColumnInfo var title: String,
    @ColumnInfo var subtitle: String? = null,
    @ColumnInfo var description: String? = null,
    @ColumnInfo var publisher: String? = null,
    @ColumnInfo var published: Int? = null,
    @ColumnInfo var coverURI: Uri? = null,
    @ColumnInfo var identifier: String? = null,
    @ColumnInfo var media: Media = Media.BOOK,
    @ColumnInfo var language: String? = null,
) : Parcelable