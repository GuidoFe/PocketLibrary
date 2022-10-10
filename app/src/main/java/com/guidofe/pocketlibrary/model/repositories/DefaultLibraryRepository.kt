package com.guidofe.pocketlibrary.model.repositories

import androidx.room.withTransaction
import com.guidofe.pocketlibrary.data.local.library_db.AppDatabase
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import javax.inject.Inject

class DefaultLibraryRepository @Inject constructor(val db: AppDatabase): LibraryRepository {
    override suspend fun insertBookBundle(bundle: BookBundle): Long {
        var bookId: Long = -1L
        withTransaction {
            bookId = insertBook(bundle.book)
            val authorsToInsert: ArrayList<Author> = arrayListOf()
            val authorsId = arrayListOf<Long>()
            bundle.authors.forEach { author ->
                if (author.authorId == 0L)
                    authorsToInsert.add(author)
                else
                    authorsId.add(author.authorId)
            }
            if(authorsToInsert.isNotEmpty()) {
                val newIds = insertAllAuthors(authorsToInsert)
                authorsId.addAll(newIds)
            }
            insertAllBookAuthors(authorsId.map{id -> BookAuthor(bookId, id) })
            val genresToInsert: ArrayList<Genre> = arrayListOf()
            val genresId = arrayListOf<Long>()
            bundle.genres.forEach { genre ->
                if (genre.genreId == 0L)
                    genresToInsert.add(genre)
                else
                    genresId.add(genre.genreId)
            }
            if(genresToInsert.isNotEmpty()) {
                val newIds = insertAllGenres(genresToInsert)
                genresId.addAll(newIds)
            }
            insertAllBookGenres(genresId.map{id -> BookGenre(bookId, id) })
            var placeId: Long? = null
            var roomId: Long? = null
            var bookshelfId: Long? = null
            if(bundle.place != null)
                placeId = if (bundle.place.placeId == 0L)
                    insertPlace(bundle.place)
                else
                    bundle.place.placeId
            if (bundle.room != null)
                roomId = if (bundle.room.roomId == 0L)
                    insertRoom(bundle.room)
                else
                    bundle.room.roomId
            if (bundle.bookshelf != null)
                bookshelfId = if (bundle.bookshelf.bookshelfId == 0L)
                    insertBookshelf(bundle.bookshelf)
                else
                    bundle.bookshelf.bookshelfId
            if(placeId != null) {
                val placement = BookPlacement(bookId, placeId, roomId, bookshelfId)
                insertBookPlacement(placement)
            }
            if(bundle.note != null)
                upsertNote(bundle.note.copy(bookId = bookId))
        }
        return bookId
    }
    override suspend fun getBookBundle(bookId: Long): BookBundle {
        return db.bookBundleDao().getBookBundle(bookId)
    }

    override suspend fun upsertBookPlacement(bookPlacement: BookPlacement) {
        db.bookPlacementDao().upsert(bookPlacement)
    }

    override suspend fun getBookBundles(pageNumber: Int, pageSize: Int): List<BookBundle?> {
        return db.bookBundleDao().getBookBundles(pageNumber, pageSize)
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return db.withTransaction(block)
    }

    override suspend fun insertBook(book: Book): Long {
        return db.bookDao().insert(book)
    }

    override suspend fun updateBook(book: Book) {
        db.bookDao().update(book)
    }

    override suspend fun insertAllAuthors(authors: List<Author>): List<Long> {
        return db.authorDao().insertAll(authors)
    }

    override suspend fun getExistingAuthors(authorsNames: List<String>): List<Author> {
        return db.authorDao().getExistingAuthors(authorsNames)
    }

    override suspend fun insertAllBookAuthors(bookAuthors: List<BookAuthor>) {
        db.bookAuthorDao().insertAll(bookAuthors)
    }

    override suspend fun insertPlace(place: Place): Long {
        return db.placeDao().insert(place)
    }

    override suspend fun getPlaceIdByName(name: String): Long? {
        return db.placeDao().getPlaceByName(name)
    }

    override suspend fun insertRoom(room: Room): Long {
        return db.roomDao().insert(room)
    }

    override suspend fun getRoomsByParentPlaceId(placeId: Long): List<Room> {
        return db.roomDao().getRoomsByParentPlaceId(placeId)
    }


    override suspend fun getRoomIdByNameAndPlaceId(name: String, placeId: Long): Long? {
        return db.roomDao().getRoomIdByNameAndPlaceId(name, placeId)
    }

    override suspend fun insertBookshelf(bookshelf: Bookshelf): Long {
        return db.bookshelfDao().insert(bookshelf)
    }

    override suspend fun getBookshelvesByParentRoomId(roomId: Long): List<Bookshelf> {
        return db.bookshelfDao().getBookshelvesByParentRoomId(roomId)
    }

    override suspend fun getBookshelfIdByNameAndRoomId(name: String, roomId: Long): Long? {
        return db.bookshelfDao().getBookshelfIdByNameAndRoomId(name, roomId)
    }

    override suspend fun insertBookPlacement(bookPlacement: BookPlacement) {
        db.bookPlacementDao().insert(bookPlacement)
    }

    override suspend fun deleteBookPlacement(bookId: Long) {
        db.bookPlacementDao().delete(bookId)
    }

    override suspend fun upsertNote(note: Note) {
        db.noteDao().upsert(note)
    }

    override suspend fun deleteNote(note: Note) {
        db.noteDao().delete(note)
    }

    override suspend fun insertAllGenres(genres: List<Genre>): List<Long> {
        return db.genreDao().insertAll(genres)
    }

    override suspend fun getGenresByNames(names: List<String>): List<Genre> {
        return db.genreDao().getGenresByNames(names)
    }

    override suspend fun insertAllBookGenres(bookGenres: List<BookGenre>) {
        db.bookGenreDao().insertAll(bookGenres)
    }

    override suspend fun getAllPlaces(): List<Place> {
        return db.placeDao().getAllPlaces()
    }

    override fun close() {
        db.close()
    }
}