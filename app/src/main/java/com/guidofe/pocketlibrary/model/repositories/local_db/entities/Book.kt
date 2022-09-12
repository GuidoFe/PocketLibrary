package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import kotlinx.parcelize.Parcelize

enum class IndustryIdentifierType {ISBN_10, ISBN_13, ISSN, OTHER}
enum class Progress {NOT_READ, SUSPENDED, IN_PROGRESS, READ}
enum class Media {BOOK, EBOOK}

@Entity
@Parcelize
data class Book (
    @PrimaryKey(autoGenerate = true) val bookId: Long,
    @ColumnInfo val title: String,
    @ColumnInfo val subtitle: String?,
    @ColumnInfo val description: String?,
    @ColumnInfo val publisher: String?,
    @ColumnInfo val published: Int?,
    @ColumnInfo val isOwned: Boolean = true,
    @ColumnInfo val progress: Progress = Progress.NOT_READ,
    @ColumnInfo val coverURI: Uri? = null,
    @ColumnInfo(name = "industry_identifier_type") val industryIdentifierType:IndustryIdentifierType = IndustryIdentifierType.ISBN_13,
    @ColumnInfo val identifier: String?,
    @ColumnInfo val media: Media = Media.BOOK,
    @ColumnInfo val score: Float? = null,
    //TODO Set default language
    @ColumnInfo val language: String = "en",
    @ColumnInfo(name = "placement_uri") val placementUri: String? = null
) : Parcelable
 // BOOK ( **IdBook**, Title, Subtitle, Description, Publisher, Published, isEbook, ISBN, Score, PlacementURI)
