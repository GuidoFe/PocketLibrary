package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.model.ImportedBookData

interface BookMetaRepository {
    fun fetchVolumesByIsbn(isbn: String, onSuccessCallback: (List<ImportedBookData>) -> Unit, onFailureCallback: (code: Int, message: String) -> Unit)
    fun searchVolumesByTitle(title: String, onSuccessCallback: (List<ImportedBookData>) -> Unit, onFailureCallback: (code: Int, message: String) -> Unit)
}