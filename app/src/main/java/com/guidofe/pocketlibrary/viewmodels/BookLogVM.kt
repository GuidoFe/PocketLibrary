package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookLogVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarState: SnackbarHostState
): ViewModel(), IBookLogVM {
    override var selectedBorrowedBook: Book? = null
    override val borrowedSelectionManager = MultipleSelectionManager<Long, BorrowedBundle>(
        getKey = {it.info.bookId}
    )

    override val borrowedItems = repo.getBorrowedBundles()
        .combine(borrowedSelectionManager.selectedItems) { books, selected ->
            books.map{
                SelectableListItem(it, selected.containsKey(it.info.bookId))
            }
        }

    override fun deleteSelectedBorrowedBooks(callback: () -> Unit) {
        viewModelScope.launch {
            repo.deleteBooksByIds(borrowedSelectionManager.selectedKeys)
            borrowedSelectionManager.clearSelection()
            callback()
        }
    }

    override fun deleteSelectedBorrowedBook(callback: () -> Unit) {
        viewModelScope.launch {
            selectedBorrowedBook?.let {
                repo.deleteBook(it)
                callback()
            }
        }
    }

    override fun updateBorrowed(borrowedBook: BorrowedBook) {
        viewModelScope.launch {
            repo.updateBorrowedBook(borrowedBook)
        }
    }

}