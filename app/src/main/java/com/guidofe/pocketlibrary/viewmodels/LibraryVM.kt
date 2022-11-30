package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.repositories.LibraryQuery
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.pages.librarypage.LibraryPageNavArgs
import com.guidofe.pocketlibrary.ui.pages.librarypage.LibraryPageState
import com.guidofe.pocketlibrary.ui.pages.navArgs
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class LibraryVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), ILibraryVM {
    override val state = LibraryPageState()
    override var duplicateIsbn: String = ""
    override val selectionManager = SelectionManager<Long, LibraryBundle>(
        getKey = { it.info.bookId }
    )
    private var currentPagingSource: PagingSource<Int, LibraryBundle>? = null
    override var customQuery: LibraryQuery? by mutableStateOf(null)
    override var pager = Pager(PagingConfig(10, initialLoadSize = 10)) {
        (
            if (customQuery == null)
                repo.getLibraryBundlesPagingSource()
            else
                repo.getLibraryBundlesWithCustomFilter(customQuery!!)
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

    init {
        val args = savedStateHandle.navArgs<LibraryPageNavArgs>()
        if (args.genre != null)
            customQuery = LibraryQuery(genre = args.genre)
    }
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

    override fun deleteSelectedBookAndRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            selectionManager.singleSelectedItem?.let {
                repo.deleteBook(it.bookBundle.book)
                currentPagingSource?.invalidate()
            }
            selectionManager.singleSelectedItem = null
        }
    }

    override fun setFavoriteAndRefresh(ids: List<Long>, favorite: Boolean, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ids.isEmpty()) return@launch
            repo.updateFavorite(ids, favorite)
            currentPagingSource?.invalidate()
            callback()
        }
    }

    override fun markSelectedBookAsLent(who: String, start: LocalDate, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            selectionManager.singleSelectedItem?.let {
                repo.insertLentBook(LentBook(it.info.bookId, who, Date.valueOf(start.toString())))
                currentPagingSource?.invalidate()
            }
            callback()
        }
    }

    override fun markSelectedItemsAsLent(who: String, start: LocalDate, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val lentBooks = selectionManager.selectedKeys.map {
                LentBook(it, who, Date.valueOf(start.toString()))
            }
            repo.insertAllLentBooks(lentBooks)
            currentPagingSource?.invalidate()
            callback()
        }
    }

    override fun markLentBookAsReturned(lentBook: LentBook) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLentBook(lentBook)
            currentPagingSource?.invalidate()
        }
    }

    override fun markSelectedLentBooksAsReturned(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteLentBooks(selectionManager.selectedItems.value.mapNotNull { it.value.lent })
            currentPagingSource?.invalidate()
            callback()
        }
    }
}
