package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
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
        onConflict: (
            booksOk: List<ImportedBookData>,
            duplicateBooks: List<ImportedBookData>
        ) -> Unit
    )

    fun saveImportedBooksAsBookBundles(importedBooks: List<ImportedBookData>, callback: () -> Unit)
    fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: (List<Long>) -> Unit
    )
    fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    val translationDialogState: TranslationDialogState
    val settingsLiveData: LiveData<AppSettings>
}