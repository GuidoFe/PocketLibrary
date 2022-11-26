package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.librarypage.LibraryPageState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ILibraryVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: SelectionManager<Long, LibraryBundle>
    fun deleteSelectedBooksAndRefresh()
    fun deleteSelectedBookAndRefresh()
    fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean)
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<LibraryBundle>>>
    fun invalidate()
    fun markSelectedItemsAsLent(who: String, start: LocalDate, callback: () -> Unit)
    fun markSelectedLentBooksAsReturned(callback: () -> Unit)
    fun markLentBookAsReturned(lentBook: LentBook)
    fun markSelectedBookAsLent(who: String, start: LocalDate, callback: () -> Unit)
    val state: LibraryPageState
}
