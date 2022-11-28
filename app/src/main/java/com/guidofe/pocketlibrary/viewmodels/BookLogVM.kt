package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.repositories.pagingsources.BorrowedBooksPagingSource
import com.guidofe.pocketlibrary.ui.pages.booklogpage.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.LentTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookLogVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarState: SnackbarHostState
) : ViewModel(), IBookLogVM {
    override val borrowedTabState = BorrowedTabState()
    override val lentTabState = LentTabState()
    override var tabIndex: Int by mutableStateOf(0)
    private var currentBorrowedPagingSource: BorrowedBooksPagingSource? = null

    override var borrowedPager = Pager(PagingConfig(10, initialLoadSize = 10)) {
        BorrowedBooksPagingSource(repo, borrowedTabState.showReturnedBooks)
            .also { currentBorrowedPagingSource = it }
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

    override val lentItems = repo.getLentLibraryBundles()
        .combine(lentTabState.selectionManager.selectedItems) { books, selected ->
            books.map {
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
}