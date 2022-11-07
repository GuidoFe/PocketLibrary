package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.OnlineBookListVM

interface ISearchBookOnlineVM {
    var author: String
    var title: String
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun search()
    val queryData: QueryData?
    val selectionManager: SelectionManager<String, ImportedBookData>
    fun saveBook(importedBook: ImportedBookData, destination: BookDestination, callback: (Long) -> Unit)
    fun saveSelectedBooks(destination: BookDestination, callback: () -> Unit)
    val listVM: OnlineBookListVM
}