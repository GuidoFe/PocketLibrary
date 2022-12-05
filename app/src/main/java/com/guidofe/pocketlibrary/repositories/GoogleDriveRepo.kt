package com.guidofe.pocketlibrary.repositories

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.guidofe.pocketlibrary.R

class GoogleDriveRepo(
    private val context: Context
) {
    companion object {
        fun getLastedSignedInAccount(context: Context): GoogleSignInAccount? {
            return GoogleSignIn.getLastSignedInAccount(context)
        }
        fun getDriverInstance(context: Context): Drive? {
            GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->
                val credential = GoogleAccountCredential.usingOAuth2(
                    context, listOf(DriveScopes.DRIVE_FILE)
                )
                credential.selectedAccount = googleAccount.account!!
                return Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName(context.getString(R.string.app_name)).build()
            }
            return null
        }
    }
    fun isUserSignedIn(): Boolean {

        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    private val googleSignInOptions = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestScopes(Scope(Scopes.DRIVE_FILE))
        .build()

    private val client = GoogleSignIn.getClient(context, googleSignInOptions)

    val signInIntent
        get() = client.signInIntent

    fun signOut(onComplete: () -> Unit = {}, onFailure: () -> Unit = {}) {
        client.signOut().addOnCompleteListener {
            onComplete()
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun getLastedSignedInAccount(): GoogleSignInAccount? {
        return getLastedSignedInAccount(context)
    }

    fun getDriverInstance(): Drive? {
        return Companion.getDriverInstance(context)
    }
}