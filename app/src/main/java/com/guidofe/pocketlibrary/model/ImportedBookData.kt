package com.guidofe.pocketlibrary.model

import android.net.Uri
import android.os.Parcelable
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.repositories.LocalRepository
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
    val media: Media = Media.BOOK,
    val language: String? = null,
    val authors: List<String> = listOf(),
    val genres: List<String> = listOf()
): Parcelable {

    private suspend fun _saveToDb(localRepo: LocalRepository): Long {
        var bookId = -1L
        val book = Book(
            bookId = 0L,
            title = title,
            subtitle = subtitle,
            description = description,
            publisher = publisher,
            published = published,
            coverURI = coverUrl?.let{Uri.parse(it)},
            identifier = identifier,
            media = media,
            language = language
        )
        bookId = localRepo.insertBook(book)
        val authorsList = authors
        val existingAuthors = localRepo.getExistingAuthors(authorsList)
        val existingAuthorsNames = existingAuthors.map{a -> a.name}
        val newAuthorsNames = authorsList.filter{a -> !existingAuthorsNames.contains(a)}
        val newAuthorsIds = localRepo.insertAllAuthors(newAuthorsNames.map{ name -> Author(0L, name) })
        val authorsIds = newAuthorsIds.plus(existingAuthors.map{a -> a.authorId})
        val bookAuthorList = authorsIds.map{id -> BookAuthor(bookId, id) }
        localRepo.insertAllBookAuthors(bookAuthorList)
        if (genres.isNotEmpty()) {
            val existingGenres = localRepo.getGenresByNames(genres)
            val existingGenresNames = existingGenres.map{g -> g.name}
            val newGenresNames= genres.filter{ g -> !existingGenresNames.contains(g)}
            val newGenresIds = localRepo.insertAllGenres(newGenresNames.map{ name ->
                Genre(0L, name)
            })
            val genresIds = newGenresIds.plus(existingGenres.map{g -> g.genreId})
            localRepo.insertAllBookGenres(genresIds.map{ id -> BookGenre(bookId, id) })
        }
        return bookId
    }
    suspend fun saveToDbAsBookBundle(localRepo: LocalRepository): Long {
        var bookId = -1L
        localRepo.withTransaction {
            bookId = _saveToDb(localRepo)
        }
        return bookId
    }

    suspend fun saveToDestination(destination: BookDestination, localRepo: LocalRepository): Long {
        var bookId = -1L
        localRepo.withTransaction {
            bookId = _saveToDb(localRepo)
            if (bookId > 0) {
                when (destination) {
                    BookDestination.LIBRARY -> localRepo.insertLibraryBook(LibraryBook(bookId))
                    BookDestination.WISHLIST -> localRepo.insertWishlistBook(WishlistBook(bookId))
                }
            }
        }
        return bookId
    }
}

