package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState

interface ISettingsVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
}