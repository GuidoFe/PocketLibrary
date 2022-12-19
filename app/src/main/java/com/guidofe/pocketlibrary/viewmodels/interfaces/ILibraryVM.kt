package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.repositories.LibraryFilter
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.library.LibraryPageState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ILibraryVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: SelectionManager<Long, LibraryBundle>
    fun deleteSelectedBooksAndRefresh()
    fun deleteSelectedBookAndRefresh()
    fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean, callback: () -> Unit)
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<LibraryBundle>>>
    fun invalidate()
    fun markSelectedBooksAsLent(who: String, start: Instant, callback: () -> Unit)
    fun markSelectedLentBooksAsReturned(callback: () -> Unit)
    fun markLentBookAsReturned(lentBook: LentBook)
    fun markSelectedBookAsLent(who: String, start: Instant, callback: () -> Unit)
    val state: LibraryPageState
    var customQuery: LibraryFilter?
    val translationState: TranslationDialogState
    val searchFieldManager: SearchFieldManager
    fun markSelectedBooksAsRead(callback: () -> Unit)
    fun markBookAsRead(bundle: BookBundle)
}
