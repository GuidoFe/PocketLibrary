package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.theme.Theme
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setLanguage(language: Language)
    val settingsFlow: Flow<AppSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setDynamicColors(enabled: Boolean)
    suspend fun setTheme(theme: Theme)
    suspend fun setMemory(isExternal: Boolean)
    fun isExternalStorageWritable(): Boolean
}