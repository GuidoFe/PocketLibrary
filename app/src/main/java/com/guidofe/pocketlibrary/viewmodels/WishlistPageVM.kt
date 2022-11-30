package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistPageVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
) : ViewModel(), IWishlistPageVM {
    override var duplicateIsbn: String = ""
    override val selectionManager = SelectionManager<Long, WishlistBundle>(
        getKey = { it.info.bookId }
    )
    private var currentPagingSource: PagingSource<Int, WishlistBundle>? = null

    override var pager = Pager(PagingConfig(10, initialLoadSize = 10)) {
        repo.getWishlistBundles().also { currentPagingSource = it }
    }.flow.cachedIn(viewModelScope).combine(selectionManager.selectedItems) { items, selected ->
        items.map {
            SelectableListItem(
                it,
                selected.containsKey(it.info.bookId)
            )
        }
    }
        private set

    override fun invalidate() {
        currentPagingSource?.invalidate()
    }

    override fun deleteSelectedBooksAndRefresh() {

        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteBooksByIds(selectionManager.selectedKeys)
            selectionManager.clearSelection()
            currentPagingSource?.invalidate()
        }
    }

    override fun deleteBookAndRefresh(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteBook(book)
            currentPagingSource?.invalidate()
        }
    }

    override fun moveBookToLibraryAndRefresh(bookId: Long, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.moveBookFromWishlistToLibrary(bookId)
            currentPagingSource?.invalidate()
            callback()
        }
    }

    override fun moveSelectedBooksToLibraryAndRefresh(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.moveBooksFromWishlistToLibrary(selectionManager.selectedKeys)
            currentPagingSource?.invalidate()
            selectionManager.clearSelection()
            callback()
        }
    }
}
