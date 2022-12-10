package com.guidofe.pocketlibrary.viewmodels.interfaces

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface IMainActivityVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val settingsLiveData: LiveData<AppSettings>
    fun getCoverDir(): Uri?
}