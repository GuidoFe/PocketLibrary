package com.guidofe.pocketlibrary.ui.pages.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.AppSettings

class SettingsState {
    var isLanguageDropdownOpen by mutableStateOf(false)
    var showThemeSelector by mutableStateOf(false)
    var currentSettings: AppSettings? by mutableStateOf(null)
    var showWaitForFileTransfer by mutableStateOf(false)
    var totalFiles by mutableStateOf(0)
    var movedFiles by mutableStateOf(0)
}