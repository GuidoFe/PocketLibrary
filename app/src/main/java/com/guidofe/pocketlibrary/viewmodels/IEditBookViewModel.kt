package com.guidofe.pocketlibrary.viewmodels

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData

interface IEditBookViewModel: IDestinationViewModel {
    var formData: FormData

    suspend fun initialiseFromDatabase(id: Long)

    suspend fun submitBook()

}