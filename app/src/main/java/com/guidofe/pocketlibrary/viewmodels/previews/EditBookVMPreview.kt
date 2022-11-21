package com.guidofe.pocketlibrary.viewmodels.previews

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookState
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM

class EditBookVMPreview : IEditBookVM {
    override var editBookState = EditBookState()
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override suspend fun initialiseFromDatabase(id: Long) {
    }

    override suspend fun submitBook(newBookDestination: BookDestination?): Long {
        return 1L
    }

    override fun updateExistingGenres(startingLetters: String) {
    }

    override fun getLocalCoverFileUri(): Uri {
        return Uri.parse("")
    }

    override fun getTempCoverUri(): Uri {
        return Uri.parse("")
    }

    override var isInitialized: Boolean = true
}