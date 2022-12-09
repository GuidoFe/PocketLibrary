package com.guidofe.pocketlibrary.repositories

import android.os.Parcelable
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibraryFilter(
    val genre: String? = null,
    val onlyFavorite: Boolean = false,
    val progress: ProgressPhase? = null,
    val mediaFilter: MediaFilter = MediaFilter.ANY,
    val language: String? = null,
    val sortingField: LibrarySortField? = null,
    val reverseOrder: Boolean = false
) : Parcelable {
    enum class MediaFilter { ANY, ONLY_BOOKS, ONLY_EBOOKS }
    enum class LibrarySortField { CREATION, TITLE }

    companion object {
        val Empty = LibraryFilter()
    }

    val isEmpty: Boolean
        get() = this == Empty
}