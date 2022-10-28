package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookDisambiguationVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDisambiguationVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
): ViewModel(), IBookDisambiguationVM