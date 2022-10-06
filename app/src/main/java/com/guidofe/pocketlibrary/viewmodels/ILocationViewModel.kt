package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ILocationViewModel {
    var placeText: String
    var roomText: String
    var bookshelfText: String
    val places: StateFlow<List<String>>
    val possibleRooms: StateFlow<List<String>>
    val possibleBookshelves: StateFlow<List<String>>
    var hasLocationBeenModified: Boolean
    fun setValues(place: String, room: String, bookshelf: String)
    fun changedPlace()
    fun changedRoom()
    fun saveLocation(bookId: Long)
}