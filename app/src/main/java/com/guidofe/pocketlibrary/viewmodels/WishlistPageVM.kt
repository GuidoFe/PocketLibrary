package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.LocalRepository
import com.guidofe.pocketlibrary.model.repositories.pagingsources.LibraryPagingSource
import com.guidofe.pocketlibrary.model.repositories.pagingsources.WishlistPagingSource
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WishlistPageVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
): ViewModel(), IWishlistPageVM {
    override var duplicateIsbn: String = ""
    override val selectionManager = MultipleSelectionManager<Long, WishlistBundle>(
        getKey = {it.wishlist.bookId}
    )
    private var currentPagingSource: WishlistPagingSource? = null

    override var pager = Pager(PagingConfig(40, initialLoadSize = 40)){
        WishlistPagingSource(repo).also { currentPagingSource = it }
    }.flow.cachedIn(viewModelScope).combine(selectionManager.selectedItems) { items, selected ->
        items.map {
            SelectableListItem(
                it,
                selected.containsKey(it.wishlist.bookId)
            )
        }
    }
        private set

    override fun onCleared() {
        super.onCleared()
    }

    override fun invalidate() {
        currentPagingSource?.invalidate()
    }

    override fun deleteSelectedBooks() {

        viewModelScope.launch {
            repo.deleteBooksByIds(selectionManager.selectedKeys)
            selectionManager.clearSelection()
            currentPagingSource?.invalidate()
        }
    }

    override fun clearSelection() {
        selectionManager.clearSelection()
    }

    override fun deleteBook(book: Book) {
        viewModelScope.launch {
            repo.deleteBook(book)
            currentPagingSource?.invalidate()
        }
    }
}
