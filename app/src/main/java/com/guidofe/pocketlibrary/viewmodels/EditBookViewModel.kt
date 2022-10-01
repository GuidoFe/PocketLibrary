package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBookViewModel @Inject constructor(
    private val repo: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    appBarState: MutableStateFlow<AppBarState?>
) : IEditBookViewModel, ViewModel() {
    //var formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
    //    private set
    var currentBookId = 0L
        private set
    override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(appBarState)
    override var formData: FormData by mutableStateOf(FormData())

    init {
        val navArgs = EditBookPageDestination.argsFrom(savedStateHandle)
        when {
            navArgs.bookBundle != null -> viewModelScope.launch {
                initialiseFromDatabase(navArgs.bookBundle)
            }
            navArgs.importedBookData != null -> initializeFromImportedBook(navArgs.importedBookData)

        }
    }
    override fun initializeFromImportedBook(importedBook: ImportedBookData) {
        currentBookId = 0L
        formData = FormData(importedBook)
    }

    override suspend fun initialiseFromDatabase(
        bookBundle: BookBundle
    ) {
        currentBookId = bookBundle.book.bookId
        formData = FormData(bookBundle)
    }

    override suspend fun submitBook() {
        //TODO: Check for validity
        repo.withTransaction {
            val book = Book(
                bookId = currentBookId,
                title = formData.title,
                subtitle = formData.subtitle.ifBlank { null },
                description = formData.description.ifBlank { null },
                publisher = formData.publisher.ifBlank { null },
                published = formData.published.toIntOrNull(),
                isOwned = formData.isOwned,
                isFavorite = formData.isFavorite,
                progress = formData.progress,
                coverURI = formData.coverUri,
                industryIdentifierType = formData.identifierType,
                identifier = formData.identifier,
                media = formData.media,
                language = formData.language
            )
            if (book.bookId == 0L) {
                currentBookId = repo.insertBook(book)
            } else {
                repo.updateBook(book)
            }
            val authorsList = formData.authors.split(",").map{a -> a.trim()}
            val existingAuthors = repo.getExistingAuthors(authorsList)
            val existingAuthorsNames = existingAuthors.map{a -> a.name}
            val newAuthorsNames = authorsList.filter{a -> !existingAuthorsNames.contains(a)}
            val newAuthorsIds = repo.insertAllAuthors(newAuthorsNames.map{name -> Author(0L, name) })
            val authorsIds = newAuthorsIds.plus(existingAuthors.map{a -> a.authorId})
            val bookAuthorList = authorsIds.map{id -> BookAuthor(currentBookId, id) }
            repo.insertAllBookAuthors(bookAuthorList)
            if (formData.place.isNotBlank()) {
                var placeId = repo.getPlaceIdByName(formData.place)
                if (placeId == null)
                    placeId = repo.insertPlace(Place(0L, formData.place))
                var roomId: Long? = null
                if (formData.room.isNotBlank()) {
                    roomId = repo.getRoomIdByNameAndPlaceId(formData.room, placeId)
                    if (roomId == null)
                        roomId = repo.insertRoom(Room(0L, formData.room, placeId))
                }
                var bookshelfId: Long? = null
                if (roomId != null && formData.bookshelf.isNotBlank()) {
                    bookshelfId = repo.getBookshelfIdByNameAndRoomId(formData.bookshelf, roomId)
                    if (bookshelfId == null) {
                        bookshelfId = repo.insertBookshelf(Bookshelf(0L, formData.bookshelf, roomId))
                    }
                }
                repo.insertBookPlacement(BookPlacement(currentBookId, placeId, roomId, bookshelfId))
            }
            if (formData.note.isNotBlank())
                repo.upsertNote(Note(currentBookId, formData.note))
            if (formData.genres.isNotEmpty()) {
                val existingGenres = repo.getGenresByNames(formData.genres)
                val existingGenresNames = existingGenres.map{g -> g.name}
                val newGenresNames= formData.genres.filter{g -> !existingGenresNames.contains(g)}
                val newGenresIds = repo.insertAllGenres(newGenresNames.map{name ->
                    Genre(0L, name)
                })
                val genresIds = newGenresIds.plus(existingGenres.map{g -> g.genreId})
               repo.insertAllBookGenres(genresIds.map{ id -> BookGenre(currentBookId, id) })
            }
        }
    }



}
