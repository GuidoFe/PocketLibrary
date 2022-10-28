package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.utils.Resource
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val libraryRepo: LibraryRepository,
    private val metaRepo: BookMetaRepository,
): ViewModel(), IImportedBookVM {
    override fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit,
    ) {
        viewModelScope.launch {
            val res = metaRepo.fetchVolumesByIsbn(isbn, maxResults)
            if (res.isSuccess())
                callback(res.data ?: listOf())
            else
                failureCallback(res.message ?: "Error")
        }
    }

    override fun getLibraryBooksWithSameIsbn(isbn: String, callback: (List<BookBundle>) -> Unit) {
        viewModelScope.launch {
            val list = libraryRepo.getBooksWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBookInDb(importedBook: ImportedBookData, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = importedBook.saveToDb(libraryRepo)
            callback(id)
        }
    }

    override fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit,
    ) {

        getImportedBooksFromIsbn(
            isbn,
            failureCallback = {
                onNetworkError()
            }
        ) { importedList ->
            when (importedList.size) {
                0 -> {
                    onNoBookFound()
                }
                1 -> {
                    saveImportedBookInDb(importedList[0]) {
                        onOneBookSaved()
                    }
                }
                else -> {
                    onMultipleBooksFound(importedList)
                }
            }
        }
    }
}