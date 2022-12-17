package com.guidofe.pocketlibrary.ui.pages.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.Language

class SettingsState {
    sealed class WifiRequester {
        data class LanguageDropdown(val language: Language) : WifiRequester()
        object TranslationSwitch : WifiRequester()
    }

    var isLanguageDropdownOpen by mutableStateOf(false)
    var isDaysBeforeDueDropdownOpen by mutableStateOf(false)
    var showThemeSelector by mutableStateOf(false)
    var currentSettings: AppSettings? by mutableStateOf(null)
    var showWaitForFileTransfer by mutableStateOf(false)
    var showAskForWifi by mutableStateOf(false)
    var isDaysBeforeDueError by mutableStateOf(false)
    var wifiRequester: WifiRequester? by mutableStateOf(null)
    var totalFiles by mutableStateOf(0)
    var movedFiles by mutableStateOf(0)
    var daysBeforeDueField: String by mutableStateOf("")
    var showTimePicker: Boolean by mutableStateOf(false)
}