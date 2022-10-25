package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.viewmodels.OnlineBookListVM
import kotlinx.coroutines.flow.Flow

interface ISearchBookOnlineVM {
    var author: String
    var title: String
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun search()
    val queryData: QueryData?
    val selectionManager: MultipleSelectionManager<String, ImportedBookData>
    fun saveBook(importedBook: ImportedBookData, callback: (Long) -> Unit)
    fun saveSelectedBooks(callback: () -> Unit)
    val listVM: OnlineBookListVM
}