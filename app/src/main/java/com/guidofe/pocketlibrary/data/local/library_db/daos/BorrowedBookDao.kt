package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import java.time.Instant
import java.time.LocalDate

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

    @Query("UPDATE borrowed_book SET who=:lender WHERE bookId IN ( :bookIds )")
    suspend fun updateLender(bookIds: List<Long>, lender: String?)

    @Query("UPDATE borrowed_book SET start=:start WHERE bookId IN ( :bookIds )")
    suspend fun updateStart(bookIds: List<Long>, start: Instant)

    @Query("UPDATE borrowed_book SET `end`=:end WHERE bookId IN ( :bookIds )")
    suspend fun updateEnd(bookIds: List<Long>, end: LocalDate?)

    @Query(
        "UPDATE borrowed_book SET notification_time=:notificationTime " +
            "WHERE bookId IN ( :bookIds )"
    )
    suspend fun updateNotificationTime(bookIds: List<Long>, notificationTime: Instant?)

    @Query("DELETE FROM borrowed_book WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("DELETE FROM borrowed_book WHERE bookId IN ( :bookIds )")
    suspend fun delete(bookIds: List<Long>)

    @Query("SELECT book.* FROM borrowed_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInBorrowedWithSameIsbn(isbn: String): List<Book>

    @Query("UPDATE borrowed_book SET isReturned = :isReturned WHERE bookId IN ( :bookIds )")
    suspend fun setReturnStatus(bookIds: List<Long>, isReturned: Boolean)

    @Query("SELECT COUNT(bookId) FROM borrowed_book WHERE isReturned = 0")
    suspend fun countCurrentlyBorrowedBooks(): Int

    @Query("SELECT COUNT(bookId) FROM borrowed_book")
    suspend fun countAllBorrowedBooks(): Int
}