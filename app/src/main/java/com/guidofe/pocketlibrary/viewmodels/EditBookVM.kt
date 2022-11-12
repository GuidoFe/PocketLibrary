package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class EditBookVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
) : ViewModel(), IEditBookVM {
    // var formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
    //    private set
    private var currentBookId: Long = 0L
    override var formData: FormData by mutableStateOf(FormData())

    override suspend fun initialiseFromDatabase(
        id: Long
    ) {
        currentBookId = id
        val bundle = repo.getBookBundle(id)
        formData = if (bundle != null) FormData(bundle) else FormData()
    }

    override fun updateExistingGenres(startingLetters: String) {
        viewModelScope.launch {
            formData.existingGenres = repo.getGenresByStartingLetters(startingLetters)
                .map { it.name }
        }
    }

    override suspend fun submitBook(newBookDestination: BookDestination?): Long {
        // TODO: Check for validity
        repo.withTransaction {
            val book = Book(
                bookId = currentBookId,
                title = formData.title,
                subtitle = formData.subtitle.ifBlank { null },
                description = formData.description.ifBlank { null },
                publisher = formData.publisher.ifBlank { null },
                published = formData.published.toIntOrNull(),
                coverURI = formData.coverUri,
                identifier = formData.identifier,
                media = formData.media,
                language = formData.language
            )
            if (book.bookId == 0L) {
                currentBookId = repo.insertBook(book)
                // TODO check for insert error
                when (newBookDestination) {
                    BookDestination.LIBRARY -> repo.insertLibraryBook(LibraryBook(currentBookId))
                    BookDestination.WISHLIST -> repo.insertWishlistBook(WishlistBook(currentBookId))
                    BookDestination.BORROWED -> repo.insertBorrowedBook(
                        BorrowedBook(currentBookId)
                    )
                    else -> {}
                }
            } else {
                repo.updateBook(book)
            }
            val authorsNames = formData.authors.split(",").map { a -> a.trim() }
            repo.insertAllAuthors(
                authorsNames.map { name ->
                    Author(0L, name)
                }
            )
            val authors = repo.getExistingAuthors(authorsNames)
            val bookAuthorList = authors.map { a ->
                BookAuthor(currentBookId, a.authorId, authorsNames.indexOf(a.name))
            }
            repo.deleteBookAuthors(currentBookId)
            repo.insertAllBookAuthors(bookAuthorList)
            if (formData.genres.isEmpty()) {
                repo.deleteBookGenreRelations(currentBookId)
            } else {
                repo.deleteBookGenreRelations(currentBookId)
                repo.insertAllGenres(formData.genres.map { Genre(0L, it) })
                val genresIds = repo.getGenresByNames(formData.genres).map { it.genreId }
                repo.insertAllBookGenres(genresIds.map { id -> BookGenre(currentBookId, id) })
            }
        }
        return currentBookId
    }
}
