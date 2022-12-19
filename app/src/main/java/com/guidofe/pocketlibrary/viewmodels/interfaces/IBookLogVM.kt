package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BookLogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklog.LentTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface IBookLogVM {
    val scaffoldState: ScaffoldState
    val snackbarState: SnackbarHostState
    val borrowedTabState: BorrowedTabState
    fun deleteBorrowedBooks(bookIds: List<Long>, callback: () -> Unit = {})
    // fun updateBorrowedBooks(borrowedBooks: List<BorrowedBook>)
    val lentTabState: LentTabState
    fun updateLent(list: List<LentBook>)
    fun removeLentStatus(books: List<LentBook>, callback: () -> Unit)
    val lentItems: Flow<List<SelectableListItem<LibraryBundle>>>
    fun setStatusOfSelectedBorrowedBooks(isReturned: Boolean)
    fun setBookReturnStatus(bookId: Long, isReturned: Boolean)
    fun moveBorrowedBooksToLibrary(bookIds: List<Long>)
    val borrowedPager: Flow<PagingData<SelectableListItem<BorrowedBundle>>>
    fun invalidateBorrowedPagingSource()
    val translationState: TranslationDialogState
    val state: BookLogState
    fun currentSearchFieldManager(): SearchFieldManager
    val lentSearchManager: SearchFieldManager
    val borrowedSearchManager: SearchFieldManager
    fun updateBorrowedBooksLender(bookIds: List<Long>, lender: String?)
    fun updateBorrowedBooksStart(bookIds: List<Long>, start: Instant)
    fun updateBorrowedBooksEnd(books: List<BorrowedBundle>, end: LocalDate?)
    fun updateNotification(bundle: BorrowedBundle, instant: Instant?)
}