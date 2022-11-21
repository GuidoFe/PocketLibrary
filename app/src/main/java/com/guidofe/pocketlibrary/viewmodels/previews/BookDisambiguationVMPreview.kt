package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookDisambiguationVM

class BookDisambiguationVMPreview : IBookDisambiguationVM {
    override val scaffoldState = ScaffoldState()
    override val snackbarHostState = SnackbarHostState()
}