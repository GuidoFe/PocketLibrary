package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState

interface IMainActivityVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
}