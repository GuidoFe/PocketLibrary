package com.guidofe.pocketlibrary.ui.pages.wishlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WishlistState {
    var isFabExpanded: Boolean by mutableStateOf(false)
    var showDoubleIsbnDialog by mutableStateOf(false)
    var isbnToSearch: String? by mutableStateOf(null)
    var showConfirmDeleteBook by mutableStateOf(false)
}