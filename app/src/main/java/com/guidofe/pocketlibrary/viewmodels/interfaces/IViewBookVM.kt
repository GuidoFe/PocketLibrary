package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IViewBookVM: IDestinationVM {
    var bundle: StateFlow<BookBundle?>
    var editedNoteFlow: MutableStateFlow<String>
    fun initBundle(bookId: Long)
    fun saveNote()
}