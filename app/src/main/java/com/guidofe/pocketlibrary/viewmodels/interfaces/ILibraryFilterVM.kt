package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.repositories.LibraryQuery
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface ILibraryFilterVM {
    fun initializeGenresList()
    val scaffoldState: ScaffoldState
    val genres: List<String>
    var title: String
    var author: String
    var genre: String
    var onlyFavorite: Boolean
    var progress: ProgressPhase?
    var mediaFilter: LibraryQuery.MediaFilter
    var language: String
    var sortingField: LibraryQuery.LibrarySortField?
    var isOrderReversed: Boolean
    var isProgressDropdownExpanded: Boolean
    var isMediaDropdownExpanded: Boolean
    var isSortDropdownExpanded: Boolean
    fun initializeState(query: LibraryQuery?)
    fun createQuery(): LibraryQuery?
}