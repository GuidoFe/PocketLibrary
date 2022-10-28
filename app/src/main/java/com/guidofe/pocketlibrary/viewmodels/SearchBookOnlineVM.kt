package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.model.repositories.pagingsources.OnlineBooksPagingSource
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchBookOnlineVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val repo: LibraryRepository,
    metaRepo: BookMetaRepository
): ViewModel(), ISearchBookOnlineVM {
    override val selectionManager = MultipleSelectionManager<String, ImportedBookData>(
        getKey = {it.externalId}
    )
    override val listVM: OnlineBookListVM = OnlineBookListVM(selectionManager, metaRepo)
    override var title: String by mutableStateOf("")
    override var author: String by mutableStateOf("")
    override fun search() {
        selectionManager.clearSelection()
        val map: MutableMap<QueryData.QueryKey, String> = mutableMapOf()
        if (title.isNotBlank())
            map[QueryData.QueryKey.intitle] = title
        if (author.isNotBlank())
            map[QueryData.QueryKey.inauthor] = author
        queryData = QueryData(null, map)
    }

    override var queryData: QueryData? by mutableStateOf(null)
        private set

    override fun saveBook(
        importedBook: ImportedBookData,
        callback: (Long) -> Unit,
    ) {
        viewModelScope.launch {
            val id = importedBook.saveToDb(repo)
            callback(id)
        }
    }

    override fun saveSelectedBooks(callback: () -> Unit) {
        viewModelScope.launch {
            selectionManager.selectedItems.value.values.forEach {
                it.saveToDb(repo)
            }
            callback()
        }
    }
}