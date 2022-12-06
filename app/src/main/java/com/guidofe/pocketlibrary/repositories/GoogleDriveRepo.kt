package com.guidofe.pocketlibrary.repositories

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.guidofe.pocketlibrary.R
import java.io.File
import java.io.OutputStream

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

    fun uploadFile(file: File, mime: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        try {
            getDriverInstance()?.let { drive ->
                val gFile = com.google.api.services.drive.model.File()
                gFile.name = file.name
                val fileContent = FileContent(mime, file)
                drive.Files().create(gFile, fileContent).execute()
                onSuccess()
            }
        } catch (e: Exception) {
            onFailure()
            e.printStackTrace()
        }
    }

    fun getFiles(stringInName: String, mimeType: String): FileList? {
        return getDriverInstance()?.Files()?.list()?.setQ(
            "name contains '$stringInName' and mimeType = '$mimeType'"
        )?.execute()
    }

    fun downloadFile(id: String, output: OutputStream) {
        getDriverInstance()?.Files()?.get(id)?.executeMediaAndDownloadTo(output)
    }
}