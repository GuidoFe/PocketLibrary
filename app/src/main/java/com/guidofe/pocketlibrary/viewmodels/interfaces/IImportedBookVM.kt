package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData

interface IImportedBookVM {
    val snackbarHostState: SnackbarHostState
    fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int = 40,
        callback: (books: List<ImportedBookData>) -> Unit,
    )

    fun getLibraryBooksWithSameIsbn(isbn: String, callback: (List<BookBundle>) -> Unit)

    fun saveImportedBookInDb(importedBook: ImportedBookData, callback: (Long) -> Unit = {})
    fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit
    )

    fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (booksOk: List<ImportedBookData>, duplicateBooks: List<ImportedBookData>) -> Unit
    )

    fun saveImportedBooksInDb(importedBooks: List<ImportedBookData>, callback: () -> Unit)
}