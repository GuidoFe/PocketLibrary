package com.guidofe.pocketlibrary.ui.pages.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class LentField { BORROWER, START }

class LibraryPageState {
    var isFabExpanded: Boolean by mutableStateOf(false)
    var isFavoriteButtonFilled by mutableStateOf(false)
    var showDoubleIsbnDialog by mutableStateOf(false)
    var isbnToSearch: String? by mutableStateOf(null)
    var showConfirmDeleteBook by mutableStateOf(false)
    var showLendBookDialog by mutableStateOf(false)
    var isContextMenuVisible by mutableStateOf(false)
    var isMenuOpen by mutableStateOf(false)
}