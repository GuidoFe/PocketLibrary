package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchBookOnlineVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val repo: LocalRepository,
    private val dataStore: DataStoreRepository,
    metaRepo: BookMetaRepository
) : ViewModel(), ISearchBookOnlineVM {
    override val selectionManager = SelectionManager<String, ImportedBookData>(
        getKey = { it.externalId }
    )
    override val listVM: IOnlineBookListVM = OnlineBookListVM(selectionManager, metaRepo)
    override var title: String by mutableStateOf("")
    override var author: String by mutableStateOf("")
    override var lang: String by mutableStateOf("")
    override val settingsFlow = dataStore.settingsLiveData
    override fun search() {
        selectionManager.clearSelection()
        val map: MutableMap<QueryData.QueryKey, String> = mutableMapOf()
        if (title.isNotBlank())
            map[QueryData.QueryKey.intitle] = title
        if (author.isNotBlank())
            map[QueryData.QueryKey.inauthor] = author
        // if (lang.isNotBlank())
        queryData = QueryData(null, map)
    }

    override var queryData: QueryData? by mutableStateOf(null)
        private set
}