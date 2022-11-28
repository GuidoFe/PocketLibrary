package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ProgressTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface IViewBookVM {
    var editedNote: String
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun initFromLocalBook(bookId: Long)
    fun saveNote(callback: () -> Unit)
    val bundle: BookBundle?
    val progTabState: ProgressTabState
    fun saveProgress(callback: () -> Unit)
}