package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import java.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowedBundleDao {
    @Transaction
    @Query("SELECT * FROM borrowed_book WHERE bookId = :bookId;")
    suspend fun getBorrowedBundle(bookId: Long): BorrowedBundle

    @Transaction
    @Query("SELECT * FROM borrowed_book WHERE isReturned = :isReturned")
    fun getBorrowedBundles(isReturned: Boolean): Flow<List<BorrowedBundle>>

    @Transaction
    @Query("SELECT * FROM borrowed_book ORDER BY isReturned")
    fun getBorrowedBundles(): Flow<List<BorrowedBundle>>

    @Transaction
    @Query("SELECT * FROM borrowed_book WHERE isReturned = 0")
    fun getBorrowedBundlesWithoutReturned(): PagingSource<Int, BorrowedBundle>

    @Transaction
    @Query("SELECT * FROM borrowed_book ORDER BY isReturned")
    fun getBorrowedBundlesWithReturned(): PagingSource<Int, BorrowedBundle>

    @Transaction
    @Query(
        "SELECT DISTINCT borrowed_book.* FROM borrowed_book NATURAL JOIN book " +
            "NATURAL JOIN bookauthor NATURAL JOIN Author WHERE lower( book.title ) LIKE " +
            "'%' || :lowerString || '%' OR lower( Author.name ) LIKE '%' || :lowerString || '%'"
    )
    fun getBorrowedBundlesByString(lowerString: String): PagingSource<Int, BorrowedBundle>

    @Transaction
    @Query(
        "SELECT borrowed_book.* FROM borrowed_book WHERE notification_time IS NOT NULL " +
            "AND notification_time > :fromInstant"
    )
    fun getBorrowedBundlesWithFutureNotification(
        fromInstant: Instant = Instant.now()
    ): List<BorrowedBundle>
}