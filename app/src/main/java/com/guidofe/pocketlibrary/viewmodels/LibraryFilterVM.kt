package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.repositories.LibraryQuery
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryFilterVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryFilterVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState
) : ViewModel(), ILibraryFilterVM {
    override var title by mutableStateOf("")
    override var author by mutableStateOf("")
    override var genre by mutableStateOf("")
    override var onlyFavorite by mutableStateOf(false)
    override var progress: ProgressPhase? by mutableStateOf(null)
    override var mediaFilter by mutableStateOf(LibraryQuery.MediaFilter.ANY)
    override var language by mutableStateOf("")
    override var sortingField: LibraryQuery.LibrarySortField? by mutableStateOf(null)
    override var isOrderReversed: Boolean by mutableStateOf(false)

    override var isProgressDropdownExpanded by mutableStateOf(false)
    override var isMediaDropdownExpanded by mutableStateOf(false)
    override var isSortDropdownExpanded by mutableStateOf(false)
    override var genres: List<String> by mutableStateOf(emptyList())
        private set

    override fun initializeGenresList() {
        viewModelScope.launch(Dispatchers.IO) {
            genres = repo.getAllGenres().map { it.name }
        }
    }

    override fun initializeState(query: LibraryQuery?) {
        title = query?.title ?: ""
        author = query?.author ?: ""
        genre = query?.genre ?: ""
        onlyFavorite = query?.onlyFavorite ?: false
        progress = query?.progress
        mediaFilter = query?.mediaFilter ?: LibraryQuery.MediaFilter.ANY
        language = query?.language ?: ""
        sortingField = query?.sortingField
        isOrderReversed = query?.reverseOrder ?: false
    }

    override fun createQuery(): LibraryQuery? {
        val query = LibraryQuery(
            title = title.ifBlank { null },
            author = author.ifBlank { null },
            genre = genre.ifBlank { null },
            onlyFavorite = onlyFavorite,
            progress = progress,
            mediaFilter = mediaFilter,
            language = language.ifBlank { null },
            sortingField = sortingField,
            reverseOrder = isOrderReversed
        )
        return if (query.isEmpty) null else query
    }
}