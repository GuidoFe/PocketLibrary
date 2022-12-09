package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SearchFieldState() {
    var value by mutableStateOf("")
    var isSearching: Boolean by mutableStateOf(false)
}
