package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class BookLogState {
    var isBorrowTabMenuExpanded: Boolean by mutableStateOf(false)
    var isLentTabMenuExpanded: Boolean by mutableStateOf(false)
    var isbnToSearch: String? by mutableStateOf(null)
    var tabIndex: Int by mutableStateOf(0)
}