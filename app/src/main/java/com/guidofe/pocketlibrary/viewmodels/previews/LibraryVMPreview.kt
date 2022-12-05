package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.repositories.LibraryQuery
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.library.LibraryPageState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

class LibraryVMPreview : ILibraryVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override val selectionManager: SelectionManager<Long, LibraryBundle>
        get() = SelectionManager { it.info.bookId }

    override fun deleteSelectedBooksAndRefresh() {
    }

    override fun deleteSelectedBookAndRefresh() {
    }

    override fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean, callback: () -> Unit) {
    }

    override var duplicateIsbn: String = ""

    override val pager: Flow<PagingData<SelectableListItem<LibraryBundle>>>
        get() = flowOf(PagingData.empty())

    override fun invalidate() {
    }

    override fun markSelectedItemsAsLent(who: String, start: LocalDate, callback: () -> Unit) {
    }

    override fun markSelectedLentBooksAsReturned(callback: () -> Unit) {
    }

    override fun markLentBookAsReturned(lentBook: LentBook) {
    }

    override fun markSelectedBookAsLent(who: String, start: LocalDate, callback: () -> Unit) {
    }

    override val state: LibraryPageState
        get() = LibraryPageState()
    override var customQuery: LibraryQuery? = null
    override val translationState: TranslationDialogState
        get() = TranslationDialogState()
}