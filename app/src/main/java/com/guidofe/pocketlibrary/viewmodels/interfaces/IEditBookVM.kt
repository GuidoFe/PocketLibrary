package com.guidofe.pocketlibrary.viewmodels.interfaces

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookState
import com.guidofe.pocketlibrary.utils.BookDestination

interface IEditBookVM {
    var state: EditBookState
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState

    suspend fun initialiseFromDatabase(id: Long)

    suspend fun submitBook(newBookDestination: BookDestination?): Long
    fun updateExistingGenres(startingLetters: String)
    fun getLocalCoverFileUri(): Uri
    fun getTempCoverUri(): Uri
    var isInitialized: Boolean
}