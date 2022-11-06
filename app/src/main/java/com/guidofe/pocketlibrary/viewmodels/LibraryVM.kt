package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.repositories.pagingsources.LibraryPagingSource
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
): ViewModel(), ILibraryVM {
    override var selectedBook: Book? = null
    override var duplicateIsbn: String = ""
    override val selectionManager = MultipleSelectionManager<Long, LibraryBundle>(
        getKey = {it.info.bookId}
    )
    private var currentPagingSource: LibraryPagingSource? = null

    override var pager = Pager(PagingConfig(40, initialLoadSize = 40)){
        LibraryPagingSource(repo).also { currentPagingSource = it }
    }.flow.cachedIn(viewModelScope).combine(selectionManager.selectedItems) { items, selected ->
        items.map {
            SelectableListItem(
                it,
                selected.containsKey(it.info.bookId)
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

    override fun deleteSelectedBooksAndRefresh() {

        viewModelScope.launch {
            repo.deleteBooksByIds(selectionManager.selectedKeys)
            selectionManager.clearSelection()
            currentPagingSource?.invalidate()
        }
    }

    override fun deleteSelectedBookAndRefresh() {
        viewModelScope.launch {
            selectedBook?.let{
                repo.deleteBook(it)
                currentPagingSource?.invalidate()
            }
            selectedBook = null
        }
    }

    override fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean) {
        viewModelScope.launch {
            if (ids.isEmpty()) return@launch
            repo.updateFavorite(ids, favorite)
            currentPagingSource?.invalidate()
        }
    }

    override fun markSelectedBookAsLent(who: String, start: LocalDate) {
        viewModelScope.launch {
            selectedBook?.let {
                repo.insertLentBook(LentBook(it.bookId, who, Date.valueOf(start.toString())))
                currentPagingSource?.invalidate()
            }
        }
    }

    override fun markSelectedItemsAsLent(who: String, start: LocalDate) {
        viewModelScope.launch {
            val lentBooks = selectionManager.selectedKeys.map{
                LentBook(it, who, Date.valueOf(start.toString()))
            }
            repo.insertAllLentBooks(lentBooks)
            currentPagingSource?.invalidate()
        }
    }

    override fun markLentBookAsReturned(lentBook: LentBook) {
        viewModelScope.launch {
            repo.deleteLentBook(lentBook)
            currentPagingSource?.invalidate()
        }
    }

    override fun markSelectedLentBooksAsReturned() {
        viewModelScope.launch {
            repo.deleteLentBooks(selectionManager.selectedItems.value.mapNotNull{it.value.lent})
        }
    }
}
