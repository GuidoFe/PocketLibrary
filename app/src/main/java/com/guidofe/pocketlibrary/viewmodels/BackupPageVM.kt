package com.guidofe.pocketlibrary.viewmodels

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.guidofe.pocketlibrary.repositories.GoogleDriveRepo
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBackupPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupPageVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
) : ViewModel(), IBackupPageVM {
    override val CONST_SIGN_IN = 1
    private var _repo: GoogleDriveRepo? = null
    override fun initRepo(context: Context) {
        if (_repo == null) _repo = GoogleDriveRepo(context)
    }
    override fun isLoggedIn(): Boolean {
        return _repo?.isUserSignedIn() ?: false
    }
    override fun getIntent(): Intent? {
        return _repo?.signInIntent
    }
    override fun handleSignInData(
        intent: Intent,
        onError: (e: Exception) -> Unit,
        onPermissionsNotGranted: () -> Unit,
        onSuccess: () -> Unit
    ) {
        GoogleSignIn.getSignedInAccountFromIntent(intent)
            .addOnCompleteListener {
                try {
                    if (!it.result.grantedScopes.contains(Scope(Scopes.DRIVE_FILE))) {
                        onPermissionsNotGranted()
                    } else {
                        onSuccess()
                    }
                } catch (e: Exception) {
                    onError(e)
                }
            }
            .addOnFailureListener {
                onError(it)
                it.printStackTrace()
            }
    }
}