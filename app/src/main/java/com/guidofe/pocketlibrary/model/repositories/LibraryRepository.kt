package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*

interface LibraryRepository {
    suspend fun insertBookBundle(bundle: BookBundle): Long?
    suspend fun getBookBundle(bookId: Long): BookBundle
    suspend fun getBookBundles(pageNumber: Int = 0, pageSize: Int): List<BookBundle>
    suspend fun <R: Any?> withTransaction(block: suspend () -> R): R
    suspend fun insertBook(book: Book): Long
    suspend fun updateBook(book: Book)
    suspend fun insertAllAuthors(authors: List<Author>): List<Long>
    suspend fun getExistingAuthors(authorsNames: List<String>): List<Author>
    suspend fun insertAllBookAuthors(bookAuthors: List<BookAuthor>)
    suspend fun insertPlace(place: Place): Long
    suspend fun getPlaceIdByName(name: String): Long?
    suspend fun insertRoom(room: Room): Long
    suspend fun getRoomIdByNameAndPlaceId(name: String, placeId: Long): Long?
    suspend fun insertBookshelf(bookshelf: Bookshelf): Long
    suspend fun getBookshelfIdByNameAndRoomId(name: String, roomId: Long): Long?
    suspend fun insertBookPlacement(bookPlacement: BookPlacement)
    suspend fun upsertNote(note: Note)
    suspend fun insertAllGenres(genres: List<Genre>): List<Long>
    suspend fun getGenresByNames(names: List<String>): List<Genre>
    suspend fun insertAllBookGenres(bookGenres: List<BookGenre>)
    fun close()
}