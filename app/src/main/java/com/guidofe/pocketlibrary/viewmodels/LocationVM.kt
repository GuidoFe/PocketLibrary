package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookPlacement
import com.guidofe.pocketlibrary.data.local.library_db.entities.Bookshelf
import com.guidofe.pocketlibrary.data.local.library_db.entities.Place
import com.guidofe.pocketlibrary.data.local.library_db.entities.Room
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILocationVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationVM @Inject constructor(
    private val repo: LibraryRepository
): ViewModel(), ILocationVM {
    override var placeText by mutableStateOf("")
    override var roomText by mutableStateOf("")
    override var bookshelfText by mutableStateOf("")
    private val _places = MutableStateFlow(listOf<String>())
    override val places: StateFlow<List<String>> = _places
    private val _possibleRooms = MutableStateFlow(listOf<String>())
    override val possibleRooms: StateFlow<List<String>> = _possibleRooms
    private val _possibleBookshelves = MutableStateFlow(listOf<String>())
    override val possibleBookshelves: StateFlow<List<String>> = _possibleBookshelves
    override var hasLocationBeenModified: Boolean by mutableStateOf(false)

    override fun changedPlace() {
        _possibleRooms.value = listOf()
        _possibleBookshelves.value = listOf()
        roomText = ""
        bookshelfText = ""
        viewModelScope.launch {
            repo.withTransaction {
                repo.getPlaceIdByName(placeText)?.let { id ->
                    _possibleRooms.value = repo.getRoomsByParentPlaceId(id).map { it.name }
                }
            }
        }
    }

    override fun changedRoom() {
        _possibleBookshelves.value = listOf()
        bookshelfText = ""
        viewModelScope.launch {
            repo.withTransaction {
                repo.getPlaceIdByName(placeText)?.let { placeId ->
                    repo.getRoomIdByNameAndPlaceId(roomText, placeId)?.let { roomId ->
                        _possibleBookshelves.value =
                            repo.getBookshelvesByParentRoomId(roomId).map { it.name }
                    }
                }
            }
        }
    }

    override fun saveLocation(bookId: Long) {
        viewModelScope.launch {
            placeText = placeText.trim()
            roomText = roomText.trim()
            bookshelfText = bookshelfText.trim()
            repo.withTransaction {
                if (placeText.isBlank()) {
                    repo.deleteBookPlacement(bookId)
                    return@withTransaction
                }
                var placeId = repo.getPlaceIdByName(placeText)
                var roomId: Long? = null
                var bookshelfId: Long? = null
                if (placeId == null)
                    placeId = repo.insertPlace(Place(0L, placeText))
                else {
                    if (roomText.isNotBlank()) {
                        roomId = repo.getRoomIdByNameAndPlaceId(roomText, placeId)
                        if (roomId == null)
                            roomId = repo.insertRoom(Room(0L, roomText, placeId))
                        else {
                            if (bookshelfText.isNotBlank()) {
                                bookshelfId = repo.getBookshelfIdByNameAndRoomId(
                                    bookshelfText,
                                    roomId
                                )
                                if (bookshelfId == null)
                                    bookshelfId = repo.insertBookshelf(
                                        Bookshelf(0L, bookshelfText, roomId)
                                    )
                            }
                        }
                    }
                }
                repo.upsertBookPlacement(BookPlacement(bookId, placeId, roomId, bookshelfId))
            }
        }
    }
    override fun setValues(place: String, room: String, bookshelf: String) {
        placeText = place
        changedPlace()
        roomText = room
        changedRoom()
        bookshelfText = bookshelf
    }

    init {
        viewModelScope.launch {
            _places.value = repo.getAllPlaces().map {it.name}
            Log.d("debug", "Size of places: ${places.value.size}")
        }
    }
}