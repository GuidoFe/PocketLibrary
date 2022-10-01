package com.guidofe.pocketlibrary.viewmodels

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData

interface IEditBookViewModel: IDestinationPage {
    var formData: FormData

    fun initializeFromImportedBook(importedBook: ImportedBookData)

    suspend fun initialiseFromDatabase(bookBundle: BookBundle)

    suspend fun submitBook()

}