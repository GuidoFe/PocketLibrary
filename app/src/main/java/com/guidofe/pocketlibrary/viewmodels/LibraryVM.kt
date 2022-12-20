package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.repositories.LibraryFilter
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.library.LibraryPageNavArgs
import com.guidofe.pocketlibrary.ui.pages.library.LibraryPageState
import com.guidofe.pocketlibrary.ui.pages.navArgs
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import com.guidofe.pocketlibrary.utils.nullIfEmptyOrBlank
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
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

    override val searchFieldManager = object : SearchFieldManager {
        override fun searchLogic() {
            invalidate()
        }
        override var searchField by mutableStateOf("")
        override var isSearching by mutableStateOf(false)
        override var shouldSearchBarRequestFocus by mutableStateOf(true)
    }
    override val translationState = TranslationDialogState()
    private var currentPagingSource: PagingSource<Int, LibraryBundle>? = null
    override var customQuery: LibraryFilter? = null
    override var pager = Pager(PagingConfig(50, initialLoadSize = 50)) {
        repo.getLibraryBundlesWithCustomFilter(
            searchFieldManager.searchField.nullIfEmptyOrBlank(),
            customQuery
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
            customQuery = LibraryFilter(genre = args.genre)
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
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun markSelectedBookAsLent(who: String, start: Instant, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            selectionManager.singleSelectedItem?.let {
                repo.insertLentBook(LentBook(it.info.bookId, who, start))
                currentPagingSource?.invalidate()
            }
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun markSelectedBooksAsLent(who: String, start: Instant, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val lentBooks = selectionManager.selectedKeys.map {
                LentBook(it, who, start)
            }
            repo.insertAllLentBooks(lentBooks)
            currentPagingSource?.invalidate()
            withContext(Dispatchers.Main) {
                callback()
            }
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
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun markSelectedBooksAsRead(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAllProgress(
                selectionManager.selectedItems.value.values.mapNotNull {
                    it.bookBundle.progress?.let { progress ->
                        progress.copy(phase = ProgressPhase.READ)
                    }
                }
            )
            repo.insertAllProgress(
                selectionManager.selectedItems.value.values.mapNotNull {
                    if (it.bookBundle.progress != null)
                        null
                    else
                        Progress(it.info.bookId, ProgressPhase.READ)
                }
            )
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    override fun markBookAsRead(bundle: BookBundle) {
        viewModelScope.launch(Dispatchers.IO) {
            if (bundle.progress == null)
                repo.insertAllProgress(
                    listOf(Progress(bundle.book.bookId, ProgressPhase.READ))
                )
            else
                repo.upsertProgress(
                    bundle.progress.copy(phase = ProgressPhase.READ)
                )
        }
    }
}
