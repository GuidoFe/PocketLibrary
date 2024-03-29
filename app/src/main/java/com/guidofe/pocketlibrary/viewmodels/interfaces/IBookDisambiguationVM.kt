package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface IBookDisambiguationVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
}