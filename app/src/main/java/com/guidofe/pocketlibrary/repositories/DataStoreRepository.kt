package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.theme.Theme
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataStoreRepository {
    suspend fun setLanguage(language: Language)
    val settingsFlow: Flow<AppSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setTheme(theme: Theme)
    suspend fun setMemory(isExternal: Boolean)
    fun isExternalStorageWritable(): Boolean
    val COVER_DIR: String
    suspend fun getCoverDir(): File?
    suspend fun getCover(fileName: String): File?
    suspend fun getCoverPath(fileName: String): String?
}