package com.guidofe.pocketlibrary.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), ISettingsVM {
    override val settingsLiveData = dataStore.settingsLiveData
    override fun setLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.Main) {
            val appLocale = LocaleListCompat.forLanguageTags(language.code)
            // delay(100)
            dataStore.setLanguage(language)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    override fun getCurrentLanguageName(): String {
        val locale = AppCompatDelegate.getApplicationLocales().getFirstMatch(
            Language.values().map { it.code }.toList().toTypedArray()
        )
        return locale?.displayLanguage?.replaceFirstChar { it.uppercase() } ?: "English"
    }

    override fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setDynamicColors(enabled)
        }
    }

    override fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setDarkTheme(enabled)
        }
    }

    override fun setTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setTheme(theme)
        }
    }

    override fun setMemory(isExternal: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setMemory(isExternal)
        }
    }

    override val hasExternalStorage: Boolean
        get() = dataStore.isExternalStorageWritable()
}