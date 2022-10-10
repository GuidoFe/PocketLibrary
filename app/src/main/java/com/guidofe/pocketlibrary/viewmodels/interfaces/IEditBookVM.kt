package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData

interface IEditBookVM: IDestinationVM {
    var formData: FormData

    suspend fun initialiseFromDatabase(id: Long)

    suspend fun submitBook()

}