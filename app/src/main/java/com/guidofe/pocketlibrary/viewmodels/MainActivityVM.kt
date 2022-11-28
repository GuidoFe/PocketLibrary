package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    val scaffoldState: ScaffoldState,
    val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel() {
    val settingsLiveData = dataStore.settingsLiveData
    fun getCoverDir(): Uri? {
        return dataStore.getCoverDir()?.toUri()
    }
}