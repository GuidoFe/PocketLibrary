package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface ILibraryVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: MultipleSelectionManager<Long, LibraryBundle>
    fun deleteSelectedBooksAndRefresh()
    fun deleteSelectedBookAndRefresh()
    fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean)
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<LibraryBundle>>>
    fun invalidate()
    var selectedBook: Book?
}
