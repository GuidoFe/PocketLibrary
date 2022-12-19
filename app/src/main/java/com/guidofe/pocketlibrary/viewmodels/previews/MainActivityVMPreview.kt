package com.guidofe.pocketlibrary.viewmodels.previews

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM

class MainActivityVMPreview : IMainActivityVM {
    override val scaffoldState: ScaffoldState
        get() = PreviewUtils.emptyScaffoldState
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override val settingsLiveData: LiveData<AppSettings>
        get() = liveData { emit(AppSettings()) }

    override fun getCoverDir(): Uri? = null
    override fun refreshNotifications() {
    }
}