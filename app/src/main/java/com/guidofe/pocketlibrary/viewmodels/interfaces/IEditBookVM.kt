package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData

interface IEditBookVM {
    var formData: FormData
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState

    suspend fun initialiseFromDatabase(id: Long)

    suspend fun submitBook(): Long
}