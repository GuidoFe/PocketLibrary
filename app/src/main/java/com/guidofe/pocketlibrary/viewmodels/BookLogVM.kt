package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BookLogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklog.LentTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SearchFieldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookLogVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarState: SnackbarHostState
) : ViewModel(), IBookLogVM {
    override val borrowedTabState = BorrowedTabState()
    override val lentTabState = LentTabState()
    override val state = BookLogState()
    private var currentBorrowedPagingSource: PagingSource<Int, BorrowedBundle>? = null
    override val translationState = TranslationDialogState()

    override var borrowedPager = Pager(PagingConfig(10, initialLoadSize = 20)) {
        (
            if (borrowedTabState.searchQuery.isBlank()) {
                repo.getBorrowedBundles(borrowedTabState.showReturnedBooks)
            } else
                repo.getBorrowedBundlesByString(borrowedTabState.searchQuery)
            ).also { currentBorrowedPagingSource = it }
    }.flow.cachedIn(viewModelScope)
        .combine(borrowedTabState.selectionManager.selectedItems) { items, selected ->
            items.map {
                SelectableListItem(
                    it,
                    selected.containsKey(it.info.bookId)
                )
            }
        }
        private set

    override fun invalidateBorrowedPagingSource() {
        currentBorrowedPagingSource?.invalidate()
    }

    override val lentItems = lentTabState.searchQuery.flatMapLatest {
        if (it.isBlank())
            repo.getLentLibraryBundles()
        else
            repo.getLentBundlesByString(it)
    }.combine(lentTabState.selectionManager.selectedItems) { list, selected ->
        list.map {
            SelectableListItem(it, selected.containsKey(it.info.bookId))
        }
    }

    override fun deleteBorrowedBooks(bookIds: List<Long>, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteBooksByIds(bookIds)
            invalidateBorrowedPagingSource()
            callback()
        }
    }

    override fun updateBorrowedBooks(borrowedBooks: List<BorrowedBook>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAllBorrowedBooks(borrowedBooks)
            invalidateBorrowedPagingSource()
        }
    }

    override fun updateLent(list: List<LentBook>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAllLentBooks(list)
        }
    }
    override fun removeLentStatus(books: List<LentBook>, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLentBooks(books)
            callback()
        }
    }

    override fun setStatusOfSelectedBorrowedBooks(isReturned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setBorrowedBookStatus(borrowedTabState.selectionManager.selectedKeys, isReturned)
            borrowedTabState.selectionManager.clearSelection()
            invalidateBorrowedPagingSource()
        }
    }

    override fun setBookReturnStatus(bookId: Long, isReturned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setBorrowedBookStatus(listOf(bookId), isReturned)
            invalidateBorrowedPagingSource()
        }
    }

    override fun moveBorrowedBooksToLibrary(bookIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteBorrowedBooks(bookIds)
            repo.insertLibraryBooks(bookIds.map { LibraryBook(it) })
            invalidateBorrowedPagingSource()
        }
    }

    override fun currentSearchFieldState(): SearchFieldState {
        return if (state.tabIndex == 0)
            borrowedTabState.searchFieldState
        else
            lentTabState.searchFieldState
    }

    override fun search() {
        if (state.tabIndex == 0) {
            borrowedTabState.searchQuery = borrowedTabState.searchFieldState.value
            currentBorrowedPagingSource?.invalidate()
        } else {
            lentTabState.searchQuery.value = lentTabState.searchFieldState.value
        }
    }
}