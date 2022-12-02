package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.ui.utils.SelectionManager

enum class BorrowedField { LENDER, START, RETURN_BY }

class BorrowedTabState {
    val selectionManager = SelectionManager<Long, BorrowedBundle> { it.info.bookId }
    val isMultipleSelecting
        get() = selectionManager.isMultipleSelecting
    var isLenderDialogVisible by mutableStateOf(false)
    var isCalendarVisible by mutableStateOf(false)
    var fieldToChange: BorrowedField? = null
    var showConfirmDeleteBook by mutableStateOf(false)
    var showReturnedBooks by mutableStateOf(false)
    var isContextMenuVisible by mutableStateOf(false)
}