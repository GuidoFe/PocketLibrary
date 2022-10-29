package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryBookDao {
    @Insert
    suspend fun insert(libraryBook: LibraryBook)

    @Query("SELECT book.* FROM library_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInLibraryWithSameIsbn(isbn: String): List<Book>

    @Query("SELECT book.* FROM library_book NATURAL JOIN book WHERE book.identifier IN ( :isbnList )")
    suspend fun getBooksInLibraryWithSameIsbns(isbnList: List<String>): List<Book>

    @Query("UPDATE library_book SET isFavorite = :isFavorite WHERE bookId = :bookId")
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)

    @Query("UPDATE library_book SET isFavorite = :isFavorite WHERE bookId IN (:bookIds)")
    suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean)
}