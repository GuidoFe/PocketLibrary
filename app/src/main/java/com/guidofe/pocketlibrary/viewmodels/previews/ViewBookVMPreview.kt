package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ProgressTabState
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM

class ViewBookVMPreview : IViewBookVM {
    override var editedNote: String = ""
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override fun initFromLocalBook(bookId: Long) {
    }

    override fun saveNote(callback: () -> Unit) {
    }

    override val bundle: BookBundle = PreviewUtils.exampleBookBundle
    override val progTabState: ProgressTabState
        get() = ProgressTabState()

    override fun saveProgress(callback: () -> Unit) {
    }
}