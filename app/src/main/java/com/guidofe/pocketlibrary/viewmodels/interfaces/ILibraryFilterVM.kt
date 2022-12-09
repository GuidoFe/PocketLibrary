package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.repositories.LibraryFilter
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface ILibraryFilterVM {
    fun initializeGenresList()
    val scaffoldState: ScaffoldState
    val genres: List<String>
    var genre: String
    var onlyFavorite: Boolean
    var progress: ProgressPhase?
    var mediaFilter: LibraryFilter.MediaFilter
    var language: String
    var sortingField: LibraryFilter.LibrarySortField?
    var isOrderReversed: Boolean
    var isProgressDropdownExpanded: Boolean
    var isMediaDropdownExpanded: Boolean
    var isSortDropdownExpanded: Boolean
    fun initializeState(query: LibraryFilter?)
    fun createQuery(): LibraryFilter?
}