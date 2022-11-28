package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class WishlistPageVMPreview : IWishlistPageVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override val selectionManager: SelectionManager<Long, WishlistBundle>
        get() = SelectionManager { it.info.bookId }
    override var duplicateIsbn: String = ""
    override val pager: Flow<PagingData<SelectableListItem<WishlistBundle>>>
        get() = emptyFlow()

    override fun invalidate() {
    }

    override fun deleteBookAndRefresh(book: Book) {
    }

    override fun deleteSelectedBooksAndRefresh() {
    }

    override fun moveBookToLibraryAndRefresh(bookId: Long, callback: () -> Unit) {
    }

    var selectedBook: Book? = null

    override fun moveSelectedBooksToLibraryAndRefresh(callback: () -> Unit) {
    }
}