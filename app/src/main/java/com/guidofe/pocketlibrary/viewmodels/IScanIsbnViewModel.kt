package com.guidofe.pocketlibrary.viewmodels

import androidx.camera.core.ImageAnalysis
import com.guidofe.pocketlibrary.model.ImportedBookData

interface IScanIsbnViewModel: IDestinationViewModel {
    var displayBookNotFoundDialog: Boolean
    var displayInsertIsbnDialog: Boolean
    var errorMessage: String?
    fun getImportedBookFromIsbn(isbn: String, callback: (book: ImportedBookData?) -> Unit, failureCallback: (code: Int, message: String) -> Unit)
    val code: String?
    fun getImageAnalysis(): ImageAnalysis
}