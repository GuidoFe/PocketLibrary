package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.utils.BookDestination

interface IImportedBookVM {
    val snackbarHostState: SnackbarHostState
    fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int = 40,
        callback: (books: List<ImportedBookData>) -> Unit,
    )

    fun saveImportedBookAsBookBundle(importedBook: ImportedBookData, callback: (Long) -> Unit = {})
    fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit,
    )

    fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (booksOk: List<ImportedBookData>, duplicateBooks: List<ImportedBookData>) -> Unit
    )

    fun saveImportedBooksAsBookBundles(importedBooks: List<ImportedBookData>, callback: () -> Unit)
    fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    )
    fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: () -> Unit
    )
    fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
}