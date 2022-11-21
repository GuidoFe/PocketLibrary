package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.LentTabState
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
    override val borrowedItems = repo.getBorrowedBundles()
        .combine(borrowedTabState.selectionManager.selectedItems) { books, selected ->
            books.map {
                SelectableListItem(it, selected.containsKey(it.info.bookId))
            }
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
            callback()
        }
    }

    override fun updateBorrowedBooks(borrowedBooks: List<BorrowedBook>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAllBorrowedBooks(borrowedBooks)
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
        }
    }
}