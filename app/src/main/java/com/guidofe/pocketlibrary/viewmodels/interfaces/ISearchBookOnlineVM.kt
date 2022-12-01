package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectionManager

interface ISearchBookOnlineVM {
    var author: String
    var title: String
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun search()
    val queryData: QueryData?
    val selectionManager: SelectionManager<String, ImportedBookData>
    val listVM: IOnlineBookListVM
    val settingsFlow: LiveData<AppSettings>
    var lang: String
}