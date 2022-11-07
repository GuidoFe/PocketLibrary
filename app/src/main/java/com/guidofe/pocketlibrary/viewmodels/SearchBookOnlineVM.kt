package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SearchBookOnlineVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val repo: LocalRepository,
    metaRepo: BookMetaRepository
) : ViewModel(), ISearchBookOnlineVM {
    override val selectionManager = SelectionManager<String, ImportedBookData>(
        getKey = { it.externalId }
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
        destination: BookDestination,
        callback: (Long) -> Unit,
    ) {
        viewModelScope.launch {
            val id = importedBook.saveToDestination(destination, repo)
            callback(id)
        }
    }

    override fun saveSelectedBooks(destination: BookDestination, callback: () -> Unit) {
        viewModelScope.launch {
            selectionManager.selectedItems.value.values.forEach {
                it.saveToDestination(destination, repo)
            }
            callback()
        }
    }
}