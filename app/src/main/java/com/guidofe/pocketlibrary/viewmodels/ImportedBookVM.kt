package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.utils.Resource
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val metaRepo: BookMetaRepository,
): ViewModel(), IImportedBookVM {
    override fun getImportedBooksFromIsbn(
        isbn: String,
        callback: (books: List<ImportedBookData>) -> Unit,
        failureCallback: (message: String) -> Unit,
        maxResults: Int
    ) {
        viewModelScope.launch {
            val res = metaRepo.fetchVolumesByIsbn(isbn, maxResults)
            if (res.isSuccess())
                callback(res.data ?: listOf())
            else
                failureCallback(res.message ?: "Error")
        }
    }

    override fun saveImportedBookInDb(importedBook: ImportedBookData, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = importedBook.saveToDb(libraryRepo)
            callback(id)
        }
    }
}