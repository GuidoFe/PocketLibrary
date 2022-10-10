package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.interfaces.IChooseImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseImportedBookVM @Inject constructor(
    private val repo: LibraryRepository,
    private val appBarState: MutableStateFlow<AppBarState?>,
): ViewModel(), IChooseImportedBookVM {
    override fun saveImportedBook(bookData: ImportedBookData, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = bookData.saveToDb(repo)
            callback(id)
        }
    }

    override val appBarDelegate = AppBarStateDelegate(appBarState)
}