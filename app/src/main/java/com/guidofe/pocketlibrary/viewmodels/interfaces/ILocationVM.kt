package com.guidofe.pocketlibrary.viewmodels.interfaces

interface ILocationVM {
    var placeText: String
    var roomText: String
    var bookshelfText: String
    val places: List<String>
    val possibleRooms: List<String>
    val possibleBookshelves: List<String>
    var hasLocationBeenModified: Boolean
    fun setPlaceValues(place: String, room: String, bookshelf: String)
    fun changedPlace()
    fun changedRoom()
    fun saveLocation(bookId: Long)
}