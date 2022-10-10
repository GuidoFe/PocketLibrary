package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.interfaces.IEditBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class EditBookVM @Inject constructor(
    private val repo: LibraryRepository,
    appBarState: MutableStateFlow<AppBarState?>
) : ViewModel(), IEditBookVM {
    //var formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
    //    private set
    var currentBookId = 0L
        private set
    override var formData: FormData = FormData()

    override suspend fun initialiseFromDatabase(
        id: Long
    ) {
        Log.d("Debug", "Initializing from database")
        val bundle = repo.getBookBundle(id)
        formData = if (bundle != null) FormData(bundle) else FormData()
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
                coverURI = formData.coverUri,
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

    override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(appBarState)
}
