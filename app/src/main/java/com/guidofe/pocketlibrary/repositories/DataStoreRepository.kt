package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setLanguage(language: Language)
    val settingsFlow: Flow<AppSettings>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setDynamicColors(enabled: Boolean)
}