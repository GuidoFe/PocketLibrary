package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.google_book.GoogleBooksService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanIsbnViewModel @Inject constructor(
   private val googleService: GoogleBooksService
): ViewModel() {
    fun getImportedBookFromIsbn(isbn: String, callback: (book: ImportedBookData?) -> Unit, failureCallback: (code: Int) -> Unit) {
        googleService.fetchVolumeByIsbn(isbn, callback, failureCallback)
    }

    fun saveImportedBookToCache(book: ImportedBookData) {

    }
}