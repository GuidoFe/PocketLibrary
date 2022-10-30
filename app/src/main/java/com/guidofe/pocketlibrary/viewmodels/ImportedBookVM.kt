package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val localRepo: LocalRepository,
    private val metaRepo: BookMetaRepository,
    override val snackbarHostState: SnackbarHostState
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

    override fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch {
            val list = localRepo.getBooksInLibraryWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val id = importedBook.saveToDestination(destination, localRepo)
            callback(id)
        }
    }

    override fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            importedBooks.forEach {
                it.saveToDestination(destination, localRepo)
            }
            callback()
        }
    }

    override fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch {
            val list = localRepo.getBooksInWishlistWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBookAsBookBundle(importedBook: ImportedBookData, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = importedBook.saveToDbAsBookBundle(localRepo)
            callback(id)
        }
    }


    override fun saveImportedBooksAsBookBundles(importedBooks: List<ImportedBookData>, callback: () -> Unit) {
        viewModelScope.launch {
            importedBooks.forEach {
                it.saveToDbAsBookBundle(localRepo)
            }
            callback()
        }
    }



    override fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
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
                    saveImportedBook(importedList[0], destination) {
                        onOneBookSaved()
                    }
                }
                else -> {
                    onMultipleBooksFound(importedList)
                }
            }
        }
    }

    override fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (booksOk: List<ImportedBookData>, duplicateBooks: List<ImportedBookData>) -> Unit
    ) {
        viewModelScope.launch {
            val conflictBooks = localRepo.getLibraryBundlesWithSameIsbns(
                list.mapNotNull { it.identifier }
            )
            val conflictIsbn = conflictBooks.map{it.bookBundle.book.identifier!!}
            if (conflictIsbn.isEmpty()) {
                onAllOk()
                return@launch
            }
            val booksOk = mutableListOf<ImportedBookData>()
            val duplicateBooks = mutableListOf<ImportedBookData>()
            list.forEach {
                if (conflictIsbn.contains(it.identifier))
                    duplicateBooks.add(it)
                else
                    booksOk.add(it)
            }
            onConflict(booksOk, duplicateBooks)
        }
    }
}