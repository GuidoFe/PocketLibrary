package com.guidofe.pocketlibrary.model

import android.net.Uri
import android.os.Parcelable
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.utils.BookDestination
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedBookData(
    val externalId: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val published: Int? = null,
    val coverUrl: String? = null,
    val identifier: String? = null,
    val isEbook: Boolean = false,
    val language: String? = null,
    val authors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val pageCount: Int? = null
) : Parcelable {

    private suspend fun saveToDb(localRepo: LocalRepository): Long {
        val book = Book(
            bookId = 0L,
            title = title,
            subtitle = subtitle,
            description = description,
            publisher = publisher,
            published = published,
            coverURI = coverUrl?.let { Uri.parse(it) },
            identifier = identifier,
            isEbook = isEbook,
            language = language,
            pageCount = pageCount
        )
        val bookId = localRepo.insertBook(book)
        localRepo.insertAllAuthors(
            authors.map { name ->
                Author(0L, name)
            }
        )
        val authorsList = localRepo.getExistingAuthors(authors)
        localRepo.insertAllBookAuthors(
            authorsList.map {
                BookAuthor(bookId, it.authorId, authors.indexOf(it.name))
            }
        )
        if (genres.isNotEmpty()) {
            val existingGenres = localRepo.getGenresByNames(genres)
            val existingGenresNames = existingGenres.map { g -> g.name }
            val newGenresNames = genres.filter { g -> !existingGenresNames.contains(g) }
            val newGenresIds = localRepo.insertAllGenres(
                newGenresNames.map { name ->
                    Genre(
                        genreId = 0L,
                        name = name,
                        englishName = name,
                        lang = "en"
                    )
                }
            )
            val genresIds = newGenresIds.plus(existingGenres.map { g -> g.genreId })
            localRepo.insertAllBookGenres(genresIds.map { id -> BookGenre(bookId, id) })
        }
        return bookId
    }

    suspend fun saveToDbAsBookBundle(localRepo: LocalRepository): Long {
        var bookId = -1L
        localRepo.withTransaction {
            bookId = saveToDb(localRepo)
        }
        return bookId
    }

    suspend fun saveToDestination(destination: BookDestination, localRepo: LocalRepository): Long {
        var bookId = -1L
        localRepo.withTransaction {
            bookId = saveToDb(localRepo)
            if (bookId > 0) {
                when (destination) {
                    BookDestination.LIBRARY -> localRepo.insertLibraryBook(LibraryBook(bookId))
                    BookDestination.WISHLIST -> localRepo.insertWishlistBook(WishlistBook(bookId))
                    BookDestination.BORROWED -> localRepo.insertBorrowedBook(
                        BorrowedBook(bookId)
                    )
                }
            }
        }
        return bookId
    }
}
