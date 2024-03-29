package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.settings.SettingsState
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM

class SettingsVMPreview : ISettingsVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override val settingsLiveData: LiveData<AppSettings>
        get() = liveData { emit(AppSettings()) }

    override fun setLanguage(language: Language) {
    }

    override fun setDynamicColors(enabled: Boolean) {
    }

    override fun setDarkTheme(enabled: Boolean) {
    }

    override fun getCurrentLanguageName(): String = "English"

    override fun setTheme(theme: Theme) {
    }

    override val hasExternalStorage: Boolean = false
    override val state: SettingsState = SettingsState().apply {
        this.currentSettings = AppSettings()
    }

    override fun setMemoryAndTransferFiles(isExternal: Boolean, callback: (Boolean) -> Unit) {
    }

    override fun setGenreTranslation(translate: Boolean) {
    }

    override val translationState: TranslationDialogState
        get() = TranslationDialogState()

    override fun setDaysBeforeDueField(s: String) {
    }

    override fun setDefaultNotificationEnabled(enabled: Boolean) {
    }

    override fun setDefaultNotificationTime(hours: Int, minutes: Int) {
    }
}