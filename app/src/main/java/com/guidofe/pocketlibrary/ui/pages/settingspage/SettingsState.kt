package com.guidofe.pocketlibrary.ui.pages.settingspage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.AppSettings



class SettingsState {
    enum class TranslationPhase {
        NO_TRANSLATING, FETCHING_GENRES, DOWNLOADING_TRANSLATOR, TRANSLATING, UPDATING_DB
    }
    var isLanguageDropdownOpen by mutableStateOf(false)
    var showThemeSelector by mutableStateOf(false)
    var currentSettings: AppSettings? by mutableStateOf(null)
    var showWaitForFileTransfer by mutableStateOf(false)
    var totalFiles by mutableStateOf(0)
    var totalGenres by mutableStateOf(0)
    var genresTranslated by mutableStateOf(0)
    var movedFiles by mutableStateOf(0)
    var translationPhase by mutableStateOf(TranslationPhase.NO_TRANSLATING)
}