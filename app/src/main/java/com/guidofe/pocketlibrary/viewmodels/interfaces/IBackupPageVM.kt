package com.guidofe.pocketlibrary.viewmodels.interfaces

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface IBackupPageVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun initRepo(context: Context)
    fun isLoggedIn(): Boolean
    fun getIntent(): Intent?
    val CONST_SIGN_IN: Int
    val dataStore: DataStoreRepository

    fun handleSignInData(
        intent: Intent,
        onError: (e: Exception) -> Unit,
        onPermissionsNotGranted: () -> Unit,
        onSuccess: () -> Unit
    )

    val isLoggedInState: Boolean
    fun signOut(onComplete: () -> Unit, onFailure: () -> Unit)
    fun backupMedia(onSuccess: () -> Unit, onFailure: () -> Unit)
    fun restoreLastBackup(onSuccess: () -> Unit, onFailure: () -> Unit)
}