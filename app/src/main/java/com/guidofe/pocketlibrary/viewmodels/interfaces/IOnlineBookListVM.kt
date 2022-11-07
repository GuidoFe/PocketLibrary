package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.flow.Flow

interface IOnlineBookListVM {
    val selectionManager: SelectionManager<String, ImportedBookData>
    val pager: Flow<PagingData<SelectableListItem<ImportedBookData>>>
    var query: QueryData?
}