package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.LentTabState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class BookLogVMPreview : IBookLogVM {
    override val borrowedItems: Flow<List<SelectableListItem<BorrowedBundle>>>
        get() = listOf(listOf<SelectableListItem<BorrowedBundle>>()).asFlow()
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarState: SnackbarHostState
        get() = SnackbarHostState()
    override val borrowedTabState: BorrowedTabState
        get() = BorrowedTabState()

    override fun deleteBorrowedBooks(bookIds: List<Long>, callback: () -> Unit) {
    }

    override fun updateBorrowedBooks(borrowedBooks: List<BorrowedBook>) {
    }

    override val lentTabState: LentTabState
        get() = LentTabState()

    override fun updateLent(list: List<LentBook>) {
    }

    override fun removeLentStatus(books: List<LentBook>, callback: () -> Unit) {
    }

    override val lentItems: Flow<List<SelectableListItem<LibraryBundle>>>
        get() = listOf(listOf<SelectableListItem<LibraryBundle>>()).asFlow()
}