package com.guidofe.pocketlibrary.model.repositories

import androidx.room.withTransaction
import com.guidofe.pocketlibrary.data.local.library_db.AppDatabase
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.utils.getInitials
import kotlinx.coroutines.flow.Flow
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
            if(bundle.note != null)
                upsertNote(bundle.note.copy(bookId = bookId))
        }
        return bookId
    }

    override suspend fun deleteBooks(books: List<Book>) {
        db.bookDao().delete(books)
    }

    override suspend fun deleteBook(book: Book) {
        db.bookDao().delete(book)
    }

    override suspend fun deleteBooksByIds(ids: List<Long>) {
        db.bookDao().deleteByIds(ids)
    }

    override suspend fun getBookBundle(bookId: Long): BookBundle {
        return db.bookBundleDao().getBookBundle(bookId)
    }


    override suspend fun getBookBundles(): Flow<List<BookBundle>> {
        return db.bookBundleDao().getBookBundles()
    }

    override suspend fun getBookBundles(pageSize: Int, pageNumber: Int): List<BookBundle> {
        return db.bookBundleDao().getBookBundles(pageNumber * pageSize, pageSize)
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

    override fun close() {
        db.close()
    }

    override suspend fun getBookBundlesWithSameTitle(
        title: String, authors: List<String>?
    ): List<BookBundle> {
        val l = db.bookBundleDao().getBookBundlesWithSameTitle(title)
        if (l.isEmpty() || authors.isNullOrEmpty()) {
            return l
        }
        val similar = mutableListOf<BookBundle>()
        val authorsInitials = authors.map{it.getInitials().joinToString()}.toSet()
        l.forEach { bundle ->
            if (
                bundle.authors.map {
                    it.name.getInitials().joinToString()
                }.toSet() == authorsInitials
            ) {
                similar.add(bundle)
            }
        }
        return similar
    }

    override suspend fun getBooksWithSameIsbn(isbn: String): List<BookBundle> {
        return db.bookBundleDao().getBookBundlesWithSameIsbn(isbn)
    }

    override suspend fun updateFavorite(bookId: Long, isFavorite: Boolean) {
        db.bookDao().updateFavorite(bookId, isFavorite)
    }

    override suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean) {
        db.bookDao().updateFavorite(bookIds, isFavorite)
    }
}