package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook

@Dao
interface BorrowedBookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(borrowedBook: BorrowedBook)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(borrowedBooks: List<BorrowedBook>)

    @Update
    suspend fun update(borrowedBook: BorrowedBook)

    @Update
    suspend fun updateAll(borrowedBooks: List<BorrowedBook>)

    @Query("DELETE FROM borrowed_book WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("DELETE FROM borrowed_book WHERE bookId IN ( :bookIds )")
    suspend fun delete(bookIds: List<Long>)

    @Query("SELECT book.* FROM borrowed_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInBorrowedWithSameIsbn(isbn: String): List<Book>

    @Query("UPDATE borrowed_book SET isReturned = :isReturned WHERE bookId IN ( :bookIds )")
    suspend fun setReturnStatus(bookIds: List<Long>, isReturned: Boolean)
}