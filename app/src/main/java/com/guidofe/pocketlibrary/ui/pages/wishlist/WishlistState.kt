package com.guidofe.pocketlibrary.ui.pages.wishlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WishlistState {
    var isExpanded: Boolean by mutableStateOf(false)
    var showDoubleIsbnDialog by mutableStateOf(false)
    var isbnToSearch: String? by mutableStateOf(null)
    var showConfirmDeleteBook by mutableStateOf(false)
    var isContextMenuVisible by mutableStateOf(false)
    var isSearching by mutableStateOf(false)
    var searchField by mutableStateOf("")
}