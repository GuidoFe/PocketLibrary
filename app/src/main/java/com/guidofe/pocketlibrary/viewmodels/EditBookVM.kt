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
import com.guidofe.pocketlibrary.ui.pages.editbook.EditBookState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.utils.Constants
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    override var state: EditBookState by mutableStateOf(EditBookState())

    override suspend fun initialiseFromDatabase(
        id: Long
    ) {
        currentBookId = id
        val bundle = repo.getBookBundle(id)
        state = if (bundle != null)
            EditBookState(bundle)
        else
            EditBookState()
    }

    override fun updateExistingGenres(startingLetters: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.existingGenres = repo.getGenresByStartingLetters(startingLetters)
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
        val lowercaseLanguage = state.language.lowercase()
        if (lowercaseLanguage.isNotBlank() &&
            !Constants.languageCodes.contains(lowercaseLanguage)
        ) {
            state.isLanguageError = true
            return -1
        }
        var uriToSave = state.coverUri
        state.coverUri?.lastPathSegment?.let { fileName ->
            if (fileName == "temp") {
                moveTempFileToCoverPath()
                state.coverUri = Uri.parse(dataStore.getCoverPath(currentBookId.toString()))
                uriToSave = Uri.parse(currentBookId.toString())
            }
        }
        repo.withTransaction {
            val book = Book(
                bookId = currentBookId,
                title = state.title,
                subtitle = state.subtitle.ifBlank { null },
                description = state.description.ifBlank { null },
                publisher = state.publisher.ifBlank { null },
                published = state.published.toIntOrNull(),
                coverURI = uriToSave,
                identifier = state.identifier,
                isEbook = state.isEbook,
                language = lowercaseLanguage.ifBlank { null }
            )
            if (book.bookId == 0L) {
                currentBookId = repo.insertBook(book)
                // TODO check for insert error
                when (newBookDestination) {
                    BookDestination.LIBRARY -> repo.insertLibraryBook(LibraryBook(currentBookId))
                    BookDestination.WISHLIST -> repo.insertWishlistBook(WishlistBook(currentBookId))
                    BookDestination.BORROWED -> repo.insertBorrowedBook(
                        BorrowedBook(currentBookId, notificationTime = null)
                    )
                    else -> {}
                }
            } else {
                repo.updateBook(book)
            }
            val authorsNames = state.authors.split(",").map { a -> a.trim() }
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
            repo.deleteBookGenreRelations(currentBookId)
            if (state.genres.isNotEmpty()) {
                val existingGenres = repo.getGenresByNames(state.genres)
                val existingGenresNames = existingGenres.map { it.name.lowercase() }
                val newGenresNames = state.genres.filter {
                    !existingGenresNames.contains(it.lowercase())
                }
                // TODO: better manage null value
                val currentLang = dataStore.settingsLiveData.value?.language?.code ?: "en"
                val newGenresIds = repo.insertAllGenres(
                    if (currentLang == "en") {
                        newGenresNames.map {
                            Genre(
                                genreId = 0L,
                                name = it,
                                englishName = it,
                                lang = "en"
                            )
                        }
                    } else {
                        newGenresNames.map {
                            Genre(
                                genreId = 0L,
                                name = it,
                                englishName = null,
                                lang = currentLang
                            )
                        }
                    }
                )
                repo.insertAllBookGenres(
                    existingGenres.map { it.genreId }.plus(newGenresIds).map {
                        BookGenre(currentBookId, it)
                    }
                )
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
