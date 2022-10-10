package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class Progress {NOT_READ, SUSPENDED, IN_PROGRESS, READ}
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
    @ColumnInfo var isOwned: Boolean = true,
    @ColumnInfo var isFavorite: Boolean = false,
    @ColumnInfo var progress: Progress = Progress.NOT_READ,
    @ColumnInfo var coverURI: Uri? = null,
    @ColumnInfo var identifier: String? = null,
    @ColumnInfo var media: Media = Media.BOOK,
    //TODO Set default language
    @ColumnInfo var language: String? = null,
) : Parcelable
 // BOOK ( **IdBook**, Title, Subtitle, Description, Publisher, Published, isEbook, ISBN, Score, PlacementURI)
