package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.theme.Theme

interface ISettingsVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val settingsLiveData: LiveData<AppSettings>
    fun setLanguage(language: Language)
    fun setDynamicColors(enabled: Boolean)
    fun setDarkTheme(enabled: Boolean)
    fun getCurrentLanguageName(): String
    fun setTheme(theme: Theme)
    fun setMemory(isExternal: Boolean)
    val hasExternalStorage: Boolean
}