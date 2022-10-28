package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import kotlinx.coroutines.launch

interface IBookDisambiguationVM {
   val scaffoldState: ScaffoldState
   val snackbarHostState: SnackbarHostState
}