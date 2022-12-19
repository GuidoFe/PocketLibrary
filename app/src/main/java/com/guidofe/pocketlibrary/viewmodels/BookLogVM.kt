package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import com.guidofe.pocketlibrary.notification.NotificationManager
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BookLogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklog.LentTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookLogVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarState: SnackbarHostState,
    private val notificationManager: NotificationManager,
    private val dataStore: DataStoreRepository
) : ViewModel(), IBookLogVM {
    override val borrowedTabState = BorrowedTabState()
    override val lentTabState = LentTabState()
    override val state = BookLogState()
    private var currentBorrowedPagingSource: PagingSource<Int, BorrowedBundle>? = null
    override val translationState = TranslationDialogState()

    override val borrowedSearchManager = object : SearchFieldManager {
        override fun searchLogic() {
            borrowedTabState.searchQuery = searchField
            currentBorrowedPagingSource?.invalidate()
        }
        override var searchField by mutableStateOf("")
        override var isSearching by mutableStateOf(false)
        override var shouldSearchBarRequestFocus by mutableStateOf(true)
    }

    override val lentSearchManager = object : SearchFieldManager {
        override fun searchLogic() {
            lentTabState.searchQuery.value = searchField
        }
        override var searchField by mutableStateOf("")
        override var isSearching by mutableStateOf(false)
        override var shouldSearchBarRequestFocus by mutableStateOf(true)
    }

    override var borrowedPager = Pager(PagingConfig(50, initialLoadSize = 50)) {
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
            for (id in bookIds) {
                notificationManager.deleteDueDateNotification(id)
            }
            repo.deleteBooksByIds(bookIds)
            invalidateBorrowedPagingSource()
            callback()
        }
    }

    override fun updateBorrowedBooksLender(bookIds: List<Long>, lender: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateBorrowedBooksLender(bookIds, lender)
        }
    }

    override fun updateBorrowedBooksStart(bookIds: List<Long>, start: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateBorrowedBooksStart(bookIds, start)
            currentBorrowedPagingSource?.invalidate()
        }
    }

    override fun updateBorrowedBooksEnd(books: List<BorrowedBundle>, end: Date?) {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = dataStore.settingsLiveData.value
            if (end == null || settings?.defaultEnableNotification == false) {
                for (book in books)
                    notificationManager.deleteDueDateNotification(book.info.bookId)
            } else {
                val daysBefore = settings?.defaultShowNotificationNDaysBeforeDue ?: 3
                val time = settings?.defaultNotificationTime ?: LocalTime.of(8, 0)
                val notificationDate = LocalDate.parse(end.toString())
                    .minusDays(daysBefore.toLong())
                val notificationDateTime = ZonedDateTime.of(
                    notificationDate, time, ZoneId.systemDefault()
                )

                for (book in books)
                    notificationManager.setDueDateNotification(
                        book, notificationDateTime.toEpochSecond()
                    )
            }
            repo.updateBorrowedBooksEnd(books.map { it.info.bookId }, end)
            currentBorrowedPagingSource?.invalidate()
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

    override fun currentSearchFieldManager(): SearchFieldManager {
        return if (state.tabIndex == 0)
            borrowedSearchManager
        else
            lentSearchManager
    }
}