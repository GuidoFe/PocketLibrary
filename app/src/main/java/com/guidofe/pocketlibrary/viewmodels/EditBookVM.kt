package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookState
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.utils.Constants
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditBookVM @Inject constructor(
    private val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), IEditBookVM {
    // var formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
    //    private set
    private var currentBookId: Long = 0L
    override var editBookState: EditBookState by mutableStateOf(EditBookState())
    override var isInitialized: Boolean = false

    override suspend fun initialiseFromDatabase(
        id: Long
    ) {
        currentBookId = id
        val bundle = repo.getBookBundle(id)
        editBookState = if (bundle != null) EditBookState(bundle) else EditBookState()
    }

    override fun updateExistingGenres(startingLetters: String) {
        viewModelScope.launch(Dispatchers.IO) {
            editBookState.existingGenres = repo.getGenresByStartingLetters(startingLetters)
                .map { it.name }
        }
    }

    override fun getLocalCoverFileUri(): Uri {
        return Uri.parse(dataStore.getCoverPath(currentBookId.toString()))
    }

    override fun getTempCoverUri(): Uri {
        return Uri.parse(dataStore.getCoverPath("temp"))
    }

    override suspend fun submitBook(newBookDestination: BookDestination?): Long {
        // TODO: Check for validity
        if (editBookState == null)
            return -1
        val lowercaseLanguage = editBookState.language.lowercase()
        if (lowercaseLanguage.isNotBlank() &&
            !Constants.languageCodes.contains(lowercaseLanguage)
        ) {
            editBookState.isLanguageError = true
            return -1
        }
        editBookState.coverUri?.lastPathSegment?.let { fileName ->
            if (fileName == "temp") {
                moveTempFileToCoverPath()
                editBookState.coverUri = getLocalCoverFileUri()
            }
        }
        repo.withTransaction {
            val book = Book(
                bookId = currentBookId,
                title = editBookState.title,
                subtitle = editBookState.subtitle.ifBlank { null },
                description = editBookState.description.ifBlank { null },
                publisher = editBookState.publisher.ifBlank { null },
                published = editBookState.published.toIntOrNull(),
                coverURI = editBookState.coverUri,
                identifier = editBookState.identifier,
                isEbook = editBookState.isEbook,
                language = lowercaseLanguage.ifBlank { null }
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
            val authorsNames = editBookState.authors.split(",").map { a -> a.trim() }
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
            if (editBookState.genres.isEmpty()) {
                repo.deleteBookGenreRelations(currentBookId)
            } else {
                repo.deleteBookGenreRelations(currentBookId)
                repo.insertAllGenres(editBookState.genres.map { Genre(0L, it) })
                val genresIds = repo.getGenresByNames(editBookState.genres).map { it.genreId }
                repo.insertAllBookGenres(genresIds.map { id -> BookGenre(currentBookId, id) })
            }
        }
        return currentBookId
    }

    private fun moveTempFileToCoverPath() {
        try {
            val tempFile = File(getTempCoverUri().path!!)
            val coverPath = getLocalCoverFileUri().path!!
            tempFile.copyTo(File(coverPath), overwrite = true)
            tempFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
