package com.guidofe.pocketlibrary.viewmodels.interfaces

import kotlinx.coroutines.flow.StateFlow

interface ILocationVM {
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