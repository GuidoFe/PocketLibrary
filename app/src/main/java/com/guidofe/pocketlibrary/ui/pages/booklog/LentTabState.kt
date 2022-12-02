package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.ui.utils.SelectionManager

enum class LentField { BORROWER, START }

class LentTabState {
    val selectionManager = SelectionManager<Long, LibraryBundle> { it.info.bookId }
    val isMultipleSelecting
        get() = selectionManager.isMultipleSelecting
    var isBorrowerDialogVisible by mutableStateOf(false)
    var isCalendarVisible by mutableStateOf(false)
    var fieldToChange: LentField? = null
    var isContextMenuVisible by mutableStateOf(false)
}