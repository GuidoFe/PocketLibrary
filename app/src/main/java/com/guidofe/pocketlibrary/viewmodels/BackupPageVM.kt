package com.guidofe.pocketlibrary.viewmodels

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.GoogleDriveRepo
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBackupPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class BackupPageVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    override val dataStore: DataStoreRepository
) : ViewModel(), IBackupPageVM {
    override val CONST_SIGN_IN = 1
    private var _gdRepo: GoogleDriveRepo? = null
    override var isLoggedInState: Boolean by mutableStateOf(isLoggedIn())
        private set
    override fun initRepo(context: Context) {
        if (_gdRepo == null) {
            _gdRepo = GoogleDriveRepo(context)
            isLoggedInState = isLoggedIn()
        }
    }
    private val zipMime = "application/zip"
    override fun isLoggedIn(): Boolean {
        return _gdRepo?.isUserSignedIn() ?: false
    }
    override fun getIntent(): Intent? {
        return _gdRepo?.signInIntent
    }
    override fun handleSignInData(
        intent: Intent,
        onError: (e: Exception) -> Unit,
        onPermissionsNotGranted: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            GoogleSignIn.getSignedInAccountFromIntent(intent)
                .addOnCompleteListener {
                    try {
                        if (!it.result.grantedScopes.contains(Scope(Scopes.DRIVE_FILE))) {
                            onPermissionsNotGranted()
                            isLoggedInState = false
                        } else {
                            isLoggedInState = true
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        isLoggedInState = false
                        onError(e)
                    }
                }
                .addOnFailureListener {
                    isLoggedInState = false
                    onError(it)
                    it.printStackTrace()
                }
        }
    }

    override fun signOut(onComplete: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _gdRepo?.signOut(
                onComplete = { isLoggedInState = false; onComplete() },
                onFailure = onFailure
            ) ?: onFailure()
        }
    }

    override fun backupMedia(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val zipFile = dataStore.settingsLiveData.value?.let { settings ->
                dataStore.saveMediaBackupLocally(settings.saveInExternal)
            }
            if (zipFile != null) {
                _gdRepo?.uploadFile(
                    zipFile,
                    zipMime,
                    onSuccess,
                    onFailure
                )
            } else onFailure()
        }
    }

    override fun restoreLastBackup(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            getLastBackupFileId()?.let {
                val file = dataStore.getFileInRootDir("backup.zip", false)
                if (file == null) {
                    onFailure()
                }
                val stream = FileOutputStream(file)
                _gdRepo?.downloadFile(it, stream)
                stream.flush()
                stream.close()
                dataStore.getCoverDir()?.path?.let { coverPath ->
                    dataStore.unzip(file!!, coverPath)
                    deleteOldCloudBackups()
                    onSuccess()
                } ?: onFailure()
            }
        }
    }

    private fun getLastBackupFileId(): String? {
        _gdRepo?.getFiles(dataStore.BACKUP_FILE_ROOT, zipMime)?.files?.let { fileList ->
            fileList.sortByDescending {
                it.name
            }
            return fileList.getOrNull(0)?.id
        }
        return null
    }

    private fun getOldBackupFilesIds(): List<String>? {
        _gdRepo?.getFiles(dataStore.BACKUP_FILE_ROOT, zipMime)?.files?.let { fileList ->
            fileList.sortByDescending {
                it.name
            }
            if (fileList.size <= 1)
                return emptyList()
            else
                return fileList.subList(1, fileList.size).map { it.id }
        }
        return null
    }

    override fun deleteOldCloudBackups() {
        viewModelScope.launch(Dispatchers.IO) {
            getOldBackupFilesIds()?.let {
                _gdRepo?.deleteFiles(it)
            }
        }
    }
}