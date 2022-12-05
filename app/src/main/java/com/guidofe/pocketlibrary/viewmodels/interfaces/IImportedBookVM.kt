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

    fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (
            booksOk: List<ImportedBookData>,
            duplicateBooks: List<ImportedBookData>
        ) -> Unit
    )

    fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit)
    val settingsLiveData: LiveData<AppSettings>
    fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit
    )

    fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?,
        callback: (List<Long>) -> Unit
    )

    fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?,
        callback: (Long) -> Unit
    )

    fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit,
        translationDialogState: TranslationDialogState?
    )
}