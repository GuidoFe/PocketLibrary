package com.guidofe.pocketlibrary.utils

import androidx.compose.ui.focus.FocusManager

interface SearchFieldManager {
    fun searchLogic()
    var searchField: String
    var isSearching: Boolean
    var shouldSearchBarRequestFocus: Boolean

    fun onSearchTriggered(focusManager: FocusManager) {
        focusManager.clearFocus()
        shouldSearchBarRequestFocus = false
        searchLogic()
        if (searchField.isBlank()) {
            isSearching = false
            shouldSearchBarRequestFocus = true
        }
    }

    fun onClosingSearch() {
        searchField = ""
        isSearching = false
        shouldSearchBarRequestFocus = true
        searchLogic()
    }
}