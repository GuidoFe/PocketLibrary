package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.BorrowedTabState
import com.guidofe.pocketlibrary.ui.pages.booklogpage.LentTabState
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface IBookLogVM {
    val borrowedItems: Flow<List<SelectableListItem<BorrowedBundle>>>
    val scaffoldState: ScaffoldState
    val snackbarState: SnackbarHostState
    val borrowedTabState: BorrowedTabState
    fun deleteBorrowedBooks(bookIds: List<Long>, callback: () -> Unit = {})
    fun updateBorrowedBooks(borrowedBooks: List<BorrowedBook>)
    val lentTabState: LentTabState
    fun updateLent(list: List<LentBook>)
    fun removeLentStatus(books: List<LentBook>, callback: () -> Unit)
    val lentItems: Flow<List<SelectableListItem<LibraryBundle>>>
}