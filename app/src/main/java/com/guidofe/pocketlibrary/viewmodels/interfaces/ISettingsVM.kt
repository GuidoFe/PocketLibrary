package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.settingspage.SettingsState
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface ISettingsVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    val settingsLiveData: LiveData<AppSettings>
    fun setLanguage(language: Language)
    fun setDynamicColors(enabled: Boolean)
    fun setDarkTheme(enabled: Boolean)
    fun getCurrentLanguageName(): String
    fun setTheme(theme: Theme)
    val hasExternalStorage: Boolean
    val state: SettingsState
    fun setMemoryAndTransferFiles(isExternal: Boolean, callback: (Boolean) -> Unit)
    fun setGenreTranslation(translate: Boolean)
    val translationState: TranslationDialogState
}