package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.paging.PagingData
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class OnlineBookListVMPreview : IOnlineBookListVM {
    override val selectionManager: SelectionManager<String, ImportedBookData>
        get() = SelectionManager { it.externalId }
    override val pager: Flow<PagingData<SelectableListItem<ImportedBookData>>>
        get() = emptyFlow()
    override var query: QueryData? = null
    override var langRestrict: String? = null
}