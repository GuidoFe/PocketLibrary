package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM

class SearchBookOnlineVMPreview : ISearchBookOnlineVM {
    override var author: String = ""
    override var title: String = ""
    override val scaffoldState = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override fun search() {
    }

    override val queryData: QueryData? = null
    override val selectionManager: SelectionManager<String, ImportedBookData>
        get() = SelectionManager { it.externalId }

    override fun saveBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    ) {
    }

    override fun saveSelectedBooks(destination: BookDestination, callback: () -> Unit) {
    }

    override val listVM: IOnlineBookListVM
        get() = OnlineBookListVMPreview()
}