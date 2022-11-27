package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase

data class LibraryQuery(
    val title: String? = null,
    val author: String? = null,
    val genre: String? = null,
    val onlyFavorite: Boolean = false,
    val progress: ProgressPhase? = null,
    val mediaFilter: MediaFilter = MediaFilter.ANY,
    val language: String? = null,
    val sortingField: LibrarySortField = LibrarySortField.Creation(true)
) {
    enum class MediaFilter { ANY, ONLY_BOOKS, ONLY_EBOOKS }
    sealed class LibrarySortField(val reverse: Boolean = false) {
        class Creation(reverse: Boolean) : LibrarySortField(reverse)
        class Title(reverse: Boolean) : LibrarySortField(reverse)
    }
    companion object {
        val Empty = LibraryQuery()
    }

    val hasWhereClauses: Boolean
        get() = title != null || author != null || genre != null || onlyFavorite ||
            progress != null || mediaFilter != MediaFilter.ANY
}