package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData

interface IImportedBookVM {
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
}