package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowedBookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(borrowedBook: BorrowedBook)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(borrowedBooks: List<BorrowedBook>)

    @Update
    suspend fun update(borrowedBook: BorrowedBook)

    @Query("DELETE FROM borrowed_book WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("DELETE FROM borrowed_book WHERE bookId IN ( :bookIds )")
    suspend fun delete(bookIds: List<Long>)

    @Query("SELECT book.* FROM borrowed_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInBorrowedWithSameIsbn(isbn: String): List<Book>
}