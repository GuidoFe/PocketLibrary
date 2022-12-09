package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.wishlist.WishlistState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import kotlinx.coroutines.flow.Flow

interface IWishlistPageVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: SelectionManager<Long, WishlistBundle>
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<WishlistBundle>>>
    fun invalidate()
    fun deleteBookAndRefresh(book: Book)
    fun deleteSelectedBooksAndRefresh()
    fun moveBookToLibraryAndRefresh(bookId: Long, callback: () -> Unit)
    fun moveSelectedBooksToLibraryAndRefresh(callback: () -> Unit)
    val translationState: TranslationDialogState
    fun search()
    val state: WishlistState
    val searchFieldManager: SearchFieldManager
}
