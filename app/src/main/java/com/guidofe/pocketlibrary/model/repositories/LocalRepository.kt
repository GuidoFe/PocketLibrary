package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun <R: Any?> withTransaction(block: suspend () -> R): R

    suspend fun insertBookBundle(bundle: BookBundle): Long
    suspend fun getBookBundle(bookId: Long): BookBundle?
    suspend fun getBookBundles(): Flow<List<BookBundle>>

    suspend fun getLibraryBundles(pageSize: Int, pageNumber: Int): List<LibraryBundle>
    suspend fun getLibraryBundlesWithSameIsbns(isbnList: List<String>): List<LibraryBundle>

    suspend fun insertAllAuthors(authors: List<Author>): List<Long>
    suspend fun getExistingAuthors(authorsNames: List<String>): List<Author>

    suspend fun insertBook(book: Book): Long
    suspend fun updateBook(book: Book)

    suspend fun insertAllBookAuthors(bookAuthors: List<BookAuthor>)

    suspend fun upsertNote(note: Note)
    suspend fun deleteNote(note: Note)

    suspend fun insertAllGenres(genres: List<Genre>): List<Long>
    suspend fun getGenresByNames(names: List<String>): List<Genre>

    suspend fun insertAllBookGenres(bookGenres: List<BookGenre>)

    fun close()
    suspend fun deleteBook(book: Book)
    suspend fun deleteBooks(books: List<Book>)

    suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean)
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)
    suspend fun deleteBooksByIds(ids: List<Long>)
    suspend fun getBookBundlesWithSameIsbn(isbn: String): List<BookBundle>
    suspend fun insertLibraryBook(libraryBook: LibraryBook)
    suspend fun getBooksInLibraryWithSameIsbn(isbn: String): List<Book>
    suspend fun getBooksInLibraryWithSameIsbns(isbns: List<String>): List<Book>
}