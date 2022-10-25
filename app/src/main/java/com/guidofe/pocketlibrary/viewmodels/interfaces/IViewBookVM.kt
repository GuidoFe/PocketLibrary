package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ViewBookImmutableData

interface IViewBookVM: ILocationVM {
    var editedNote: String
    val data: ViewBookImmutableData?
    val scaffoldState: ScaffoldState
    fun initFromLibraryBook(bookId: Long)
    fun saveNote(bookId: Long)
}