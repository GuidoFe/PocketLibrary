package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM

class SearchBookOnlineVMPreview : ISearchBookOnlineVM {
    override var author: String = ""
    override var title: String = ""
    override val scaffoldState = PreviewUtils.emptyScaffoldState
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override fun search() {
    }

    override val queryData: QueryData? = null
    override val selectionManager: SelectionManager<String, ImportedBookData>
        get() = SelectionManager { it.externalId }

    override val listVM: IOnlineBookListVM
        get() = OnlineBookListVMPreview()
    override val settingsFlow: LiveData<AppSettings>
        get() = liveData { emit(AppSettings()) }
    override var langField: String = ""
    override val langRestrict: String?
        get() = null
    override val translationState: TranslationDialogState
        get() = TranslationDialogState()
}