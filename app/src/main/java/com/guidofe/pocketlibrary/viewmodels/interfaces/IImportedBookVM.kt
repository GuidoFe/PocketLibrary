package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.model.ImportedBookData

interface IImportedBookVM {
    fun getImportedBooksFromIsbn(
        isbn: String,
        callback: (books: List<ImportedBookData>) -> Unit,
        failureCallback: (message: String) -> Unit,
        maxResults: Int = 40
    )
    fun saveImportedBookInDb(importedBook: ImportedBookData, callback: (Long) -> Unit)
}