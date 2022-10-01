package com.guidofe.pocketlibrary.data.local.library_db.entities

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

enum class IndustryIdentifierType {ISBN_10, ISBN_13, ISSN, OTHER}
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
    @ColumnInfo(name = "industry_identifier_type") var industryIdentifierType: IndustryIdentifierType = IndustryIdentifierType.ISBN_13,
    @ColumnInfo var identifier: String? = null,
    @ColumnInfo var media: Media = Media.BOOK,
    //TODO Set default language
    @ColumnInfo var language: String = "en",
) : Parcelable
 // BOOK ( **IdBook**, Title, Subtitle, Description, Publisher, Published, isEbook, ISBN, Score, PlacementURI)
