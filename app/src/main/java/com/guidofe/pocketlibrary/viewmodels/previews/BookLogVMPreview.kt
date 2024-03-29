package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BookLogState
import com.guidofe.pocketlibrary.ui.pages.booklog.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklog.LentTabState
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.utils.SearchFieldManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow

class BookLogVMPreview() : IBookLogVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarState: SnackbarHostState
        get() = SnackbarHostState()
    override val borrowedTabState: BorrowedTabState
        get() = BorrowedTabState()

    override fun deleteBorrowedBooks(bookIds: List<Long>, callback: () -> Unit) {
    }

    override val lentTabState: LentTabState
        get() = LentTabState()

    override fun updateLent(list: List<LentBook>) {
    }

    override fun removeLentStatus(books: List<LentBook>, callback: () -> Unit) {
    }

    override val lentItems: Flow<List<SelectableListItem<LibraryBundle>>>
        get() = listOf(listOf<SelectableListItem<LibraryBundle>>()).asFlow()

    override fun setStatusOfSelectedBorrowedBooks(isReturned: Boolean) {
    }

    override fun setBookReturnStatus(bookId: Long, isReturned: Boolean) {
    }

    override fun moveBorrowedBooksToLibrary(bookIds: List<Long>) {
    }

    override val borrowedPager: Flow<PagingData<SelectableListItem<BorrowedBundle>>>
        get() = emptyFlow()

    override fun invalidateBorrowedPagingSource() {
    }

    override val translationState: TranslationDialogState
        get() = TranslationDialogState()

    override val state: BookLogState
        get() = BookLogState()

    override fun currentSearchFieldManager(): SearchFieldManager = borrowedSearchManager
    override val lentSearchManager: SearchFieldManager
        get() = PreviewUtils.emptySearchFieldManager
    override val borrowedSearchManager: SearchFieldManager
        get() = PreviewUtils.emptySearchFieldManager

    override fun updateBorrowedBooksLender(bookIds: List<Long>, lender: String?) {
    }

    override fun updateBorrowedBooksStart(bookIds: List<Long>, start: Instant) {
    }

    override fun updateBorrowedBooksEnd(books: List<BorrowedBundle>, end: LocalDate?) {
    }

    override fun updateNotification(bundle: BorrowedBundle, instant: Instant?) {
    }
}