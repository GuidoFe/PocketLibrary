package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import kotlinx.coroutines.flow.Flow

interface ISettingsVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val settingsFlow: Flow<AppSettings>
    fun setLanguage(language: Language)
    fun setDynamicColors(enabled: Boolean)
    fun setDarkTheme(enabled: Boolean)
    fun getCurrentLanguageName(): String
}