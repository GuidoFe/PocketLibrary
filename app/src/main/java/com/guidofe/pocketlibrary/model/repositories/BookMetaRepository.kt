package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.model.ImportedBookData

interface BookMetaRepository {
    fun fetchVolumeByIsbn(isbn: String, onSuccessCallback: (ImportedBookData?) -> Unit, onFailureCallback: (code: Int, message: String) -> Unit)
}