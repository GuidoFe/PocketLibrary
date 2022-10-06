package com.guidofe.pocketlibrary.viewmodels

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IViewBookViewModel: IDestinationViewModel {
    var bundle: StateFlow<BookBundle?>
    var editedNoteFlow: MutableStateFlow<String>
    fun initBundle(bookId: Long)
    fun saveNote()
}