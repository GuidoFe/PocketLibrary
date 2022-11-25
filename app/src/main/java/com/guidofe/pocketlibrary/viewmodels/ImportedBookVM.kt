package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val localRepo: LocalRepository,
    private val metaRepo: BookMetaRepository,
    override val snackbarHostState: SnackbarHostState,
    private val imageLoader: ImageLoader
) : ViewModel(), IImportedBookVM {
    override fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = metaRepo.fetchVolumesByIsbn(isbn, maxResults)
            if (res.isSuccess())
                callback(res.data ?: emptyList())
            else
                failureCallback(res.message ?: "Error")
        }
    }

    override fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInLibraryWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = importedBook.saveToDestination(destination, localRepo)
            callback(id)
        }
    }

    override fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            importedBooks.forEach {
                it.saveToDestination(destination, localRepo)
            }
            callback()
        }
    }

    override fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInWishlistWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInBorrowedWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBookAsBookBundle(
        importedBook: ImportedBookData,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = importedBook.saveToDbAsBookBundle(localRepo)
            callback(id)
        }
    }

    override fun saveImportedBooksAsBookBundles(
        importedBooks: List<ImportedBookData>,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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
        onConflict: (
            booksOk: List<ImportedBookData>,
            duplicateBooks: List<ImportedBookData>
        ) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val conflictBooks = localRepo.getLibraryBundlesWithSameIsbns(
                list.mapNotNull { it.identifier }
            )
            val conflictIsbn = conflictBooks.map { it.bookBundle.book.identifier!! }
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