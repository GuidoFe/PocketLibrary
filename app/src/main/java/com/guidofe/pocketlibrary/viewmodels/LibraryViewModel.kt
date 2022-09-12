package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.google_book.GoogleBooksService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val googleService: GoogleBooksService
): ViewModel() {
    var showInsertIsbnDialog = mutableStateOf(false)

    fun fetchBookForTypedIsbn(isbn: String,
                              callback: (ImportedBookData?) -> Unit,
                              failureCallback: (Int) -> Unit = {}) {
        googleService.fetchVolumeByIsbn(isbn, callback, failureCallback)
    }
}
