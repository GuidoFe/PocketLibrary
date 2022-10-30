package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface IWishlistPageVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val selectionManager: MultipleSelectionManager<Long, WishlistBundle>
    fun deleteSelectedBooks()
    fun deleteBook(book: Book)
    fun clearSelection()
    var duplicateIsbn: String
    val pager: Flow<PagingData<SelectableListItem<WishlistBundle>>>
    fun invalidate()
}
