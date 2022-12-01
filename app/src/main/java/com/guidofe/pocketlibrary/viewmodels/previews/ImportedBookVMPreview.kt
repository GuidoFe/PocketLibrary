package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM

class ImportedBookVMPreview : IImportedBookVM {
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit
    ) {
    }

    override fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit
    ) {
    }

    override fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (
            booksOk: List<ImportedBookData>,
            duplicateBooks: List<ImportedBookData>
        ) -> Unit
    ) {
    }

    override fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
    }

    override fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: (List<Long>) -> Unit
    ) {
    }

    override fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
    }

    override fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
    }

    override val translationDialogState: TranslationDialogState
        get() = TODO("Not yet implemented")
    override val settingsLiveData: LiveData<AppSettings>
        get() = TODO("Not yet implemented")

    override fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    ) { callback(1L) }
}