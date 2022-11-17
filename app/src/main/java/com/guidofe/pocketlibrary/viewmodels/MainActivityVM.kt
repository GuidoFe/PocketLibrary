package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    dataStore: DataStoreRepository
) : ViewModel(), IMainActivityVM {
    val settingsFlow = dataStore.settingsFlow
}