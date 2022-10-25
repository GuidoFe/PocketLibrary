package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ViewBookImmutableData
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewBookVM @Inject constructor(
    val repo: LibraryRepository,
    override val scaffoldState: ScaffoldState
): ViewModel(), IViewBookVM {
    private var oldNote: Note? = null
    override var editedNote: String by mutableStateOf("")
    override var data: ViewBookImmutableData? by mutableStateOf(null)
        private set
    override var placeText: String by mutableStateOf("")
    override var roomText by mutableStateOf("")
    override var bookshelfText by mutableStateOf("")
    override var places: List<String> by mutableStateOf(listOf())
        private set
    override var possibleRooms: List<String> by mutableStateOf(listOf())
        private set
    override var possibleBookshelves: List<String> by mutableStateOf(listOf())
        private set
    override var hasLocationBeenModified: Boolean by mutableStateOf(false)

    override fun initFromLibraryBook(bookId: Long) {
        //TODO: What to do if book doesn't exist?
        viewModelScope.launch {
            val bookBundle = repo.getBookBundle(bookId)
            bookBundle?.let { bundle ->
                data = ViewBookImmutableData(bundle)
                oldNote = bundle.note
                editedNote = oldNote?.note ?: ""
                setPlaceValues(
                    bundle.place?.name ?: "",
                    bundle.room?.name ?: "",
                    bundle.bookshelf?.name ?: ""
                )
            }
        }
    }

    override fun saveNote(bookId: Long) {
        viewModelScope.launch {
            if(bookId > 0) {
                if (editedNote.isBlank() && oldNote != null) {
                    repo.deleteNote(oldNote!!)
                } else {
                    if(editedNote.isNotBlank())
                        repo.upsertNote(Note(bookId, editedNote))
                }
            }
        }
    }


    override fun changedPlace() {
        possibleRooms = listOf()
        possibleBookshelves = listOf()
        roomText = ""
        bookshelfText = ""
        viewModelScope.launch {
            repo.withTransaction {
                repo.getPlaceIdByName(placeText)?.let { id ->
                    possibleRooms = repo.getRoomsByParentPlaceId(id).map { it.name }
                }
            }
        }
    }

    override fun changedRoom() {
        possibleBookshelves = listOf()
        bookshelfText = ""
        viewModelScope.launch {
            repo.withTransaction {
                repo.getPlaceIdByName(placeText)?.let { placeId ->
                    repo.getRoomIdByNameAndPlaceId(roomText, placeId)?.let { roomId ->
                        possibleBookshelves =
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
                repo.upsertBookPlacement(
                    BookPlacement(bookId, placeId, roomId, bookshelfId)
                )
            }
        }
    }
    override fun setPlaceValues(place: String, room: String, bookshelf: String) {
        placeText = place
        changedPlace()
        roomText = room
        changedRoom()
        bookshelfText = bookshelf
    }

    init {
        viewModelScope.launch {
            places = repo.getAllPlaces().map {it.name}
            Log.d("debug", "Size of places: ${places.size}")
        }
    }

}