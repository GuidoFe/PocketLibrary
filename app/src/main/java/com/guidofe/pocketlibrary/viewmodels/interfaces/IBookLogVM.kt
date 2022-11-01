package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface IBookLogVM {
    val borrowedItems: Flow<List<SelectableListItem<BorrowedBundle>>>
    val borrowedSelectionManager: MultipleSelectionManager<Long, BorrowedBundle>
    var selectedBorrowedBook: Book?
}