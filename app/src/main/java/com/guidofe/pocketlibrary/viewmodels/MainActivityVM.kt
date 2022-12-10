package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), IMainActivityVM {
    override val settingsLiveData = dataStore.settingsLiveData
    override fun getCoverDir(): Uri? {
        return dataStore.getCoverDir()?.toUri()
    }
}