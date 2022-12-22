package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.notification.AppNotificationManager
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository,
    private val nm: AppNotificationManager,
    private val repo: LocalRepository
) : ViewModel(), IMainActivityVM {
    override val settingsLiveData = dataStore.settingsLiveData
    override fun getCoverDir(): Uri? {
        return dataStore.getCoverDir()?.toUri()
    }
    override fun refreshNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            val bundles = repo.getBorrowedBundlesWithFutureNotification()
            for (bundle in bundles) {
                bundle.info.notificationTime?.let { instant ->
                    nm.setDueDateNotification(bundle, instant.toEpochMilli())
                }
            }
        }
    }
}