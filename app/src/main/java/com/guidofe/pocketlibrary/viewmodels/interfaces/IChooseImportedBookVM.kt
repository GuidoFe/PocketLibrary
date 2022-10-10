package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.model.ImportedBookData

interface IChooseImportedBookVM: IDestinationVM {
    fun saveImportedBook(bookData: ImportedBookData, callback: (Long) -> Unit)
}