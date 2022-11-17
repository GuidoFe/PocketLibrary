package com.guidofe.pocketlibrary.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), ISettingsVM {
    override val settingsFlow = dataStore.settingsFlow
    override fun setLanguage(language: Language) {
        viewModelScope.launch {
            val appLocale = LocaleListCompat.forLanguageTags(language.code)
            // delay(100)
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
        viewModelScope.launch {
            dataStore.setDynamicColors(enabled)
        }
    }

    override fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkTheme(enabled)
        }
    }
}