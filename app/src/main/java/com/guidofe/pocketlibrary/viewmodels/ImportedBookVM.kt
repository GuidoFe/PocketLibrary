package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val metaRepo: BookMetaRepository,
): ViewModel(), IImportedBookVM {
    override fun getImportedBooksFromIsbn(isbn: String, callback: (books: List<ImportedBookData>) -> Unit, failureCallback: (code: Int, message: String) -> Unit) {
        metaRepo.fetchVolumesByIsbn(isbn, callback, failureCallback)
    }

    override fun saveImportedBookInDb(importedBook: ImportedBookData, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = importedBook.saveToDb(libraryRepo)
            callback(id)
        }
    }
}