package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.model.repositories.pagingsources.OnlineBooksPagingSource
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnlineBookListVM(
    override val selectionManager: MultipleSelectionManager<String, ImportedBookData>,
    private val metaRepo: BookMetaRepository
): ViewModel(), IOnlineBookListVM {
    override var query: QueryData? = null
    override var pager = Pager(PagingConfig(20, initialLoadSize = 20)){
        OnlineBooksPagingSource(query, repo = metaRepo)
    }.flow.map{ pagingData ->
        val idSet: HashSet<String> = hashSetOf()
        pagingData.filter {
            if (idSet.contains(it.externalId))
                false
            else {
                idSet.add(it.externalId)
                true
            }
        }
    }.cachedIn(viewModelScope).combine(selectionManager.selectedItems) { items, selected ->
        items.map {
            SelectableListItem(
                it,
                selected.containsKey(it.externalId)
            )
        }
    }
        private set
}