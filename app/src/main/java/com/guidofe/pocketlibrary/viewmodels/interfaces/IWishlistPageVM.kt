package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface IWishlistPageVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: MultipleSelectionManager<Long, WishlistBundle>
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<WishlistBundle>>>
    fun invalidate()
    fun deleteSelectedBookAndRefresh()
    fun deleteSelectedBooksAndRefresh()
    fun moveBookToLibraryAndRefresh(bookId: Long, callback: () -> Unit)
    var selectedBook: Book?
    fun moveSelectedBooksToLibraryAndRefresh(callback: () -> Unit)
}
