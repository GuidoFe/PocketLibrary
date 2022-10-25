package com.guidofe.pocketlibrary.model

import android.net.Uri
import android.os.Parcelable
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
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
    suspend fun saveToDb(libraryRepo: LibraryRepository): Long {
        var bookId = -1L
        libraryRepo.withTransaction {
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
            bookId = libraryRepo.insertBook(book)
            val authorsList = authors
            val existingAuthors = libraryRepo.getExistingAuthors(authorsList)
            val existingAuthorsNames = existingAuthors.map{a -> a.name}
            val newAuthorsNames = authorsList.filter{a -> !existingAuthorsNames.contains(a)}
            val newAuthorsIds = libraryRepo.insertAllAuthors(newAuthorsNames.map{name -> Author(0L, name) })
            val authorsIds = newAuthorsIds.plus(existingAuthors.map{a -> a.authorId})
            val bookAuthorList = authorsIds.map{id -> BookAuthor(bookId, id) }
            libraryRepo.insertAllBookAuthors(bookAuthorList)
            if (genres.isNotEmpty()) {
                val existingGenres = libraryRepo.getGenresByNames(genres)
                val existingGenresNames = existingGenres.map{g -> g.name}
                val newGenresNames= genres.filter{ g -> !existingGenresNames.contains(g)}
                val newGenresIds = libraryRepo.insertAllGenres(newGenresNames.map{name ->
                    Genre(0L, name)
                })
                val genresIds = newGenresIds.plus(existingGenres.map{g -> g.genreId})
                libraryRepo.insertAllBookGenres(genresIds.map{ id -> BookGenre(bookId, id) })
            }
        }
        return bookId
    }
}

