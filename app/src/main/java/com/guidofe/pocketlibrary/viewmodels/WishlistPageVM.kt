package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.wishlist.WishlistState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class WishlistPageVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
) : ViewModel(), IWishlistPageVM {
    override var duplicateIsbn: String = ""
    override val translationState = TranslationDialogState()
    override val state = WishlistState()
    override val selectionManager = SelectionManager<Long, WishlistBundle>(
        getKey = { it.info.bookId }
    )
    override val searchFieldManager = object : SearchFieldManager {
        override fun searchLogic() {
            invalidate()
        }
        override var searchField by mutableStateOf("")
        override var isSearching by mutableStateOf(false)
        override var shouldSearchBarRequestFocus by mutableStateOf(true)
    }

    private var currentPagingSource: PagingSource<Int, WishlistBundle>? = null

    override var pager = Pager(PagingConfig(50, initialLoadSize = 50)) {
        (
            if (searchFieldManager.searchField.isNotBlank())
                repo.getWishlistBundlesByString(searchFieldManager.searchField)
            else
                repo.getWishlistBundles()
            ).also { currentPagingSource = it }
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
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun moveSelectedBooksToLibraryAndRefresh(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.moveBooksFromWishlistToLibrary(selectionManager.selectedKeys)
            currentPagingSource?.invalidate()
            selectionManager.clearSelection()
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun search() {
        currentPagingSource?.invalidate()
    }
}
