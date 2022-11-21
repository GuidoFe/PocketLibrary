package com.guidofe.pocketlibrary.repositories

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.theme.Theme
import java.io.File

interface DataStoreRepository {
    suspend fun setLanguage(language: Language)
    val settingsLiveData: LiveData<AppSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setTheme(theme: Theme)
    suspend fun setMemory(isExternal: Boolean)
    fun isExternalStorageWritable(): Boolean
    val COVER_DIR: String
    fun getCoverDir(): File?
    fun getCover(fileName: String): File?
    fun getCoverPath(fileName: String): String?
    suspend fun saveCover(bitmap: Bitmap, path: String, callback: () -> Unit)
}