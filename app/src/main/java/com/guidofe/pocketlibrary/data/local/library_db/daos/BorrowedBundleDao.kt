package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import kotlinx.coroutines.flow.Flow

@Dao
interface BorrowedBundleDao {
    @Transaction
    @Query("SELECT * FROM borrowed_book WHERE bookId = :bookId;")
    suspend fun getBorrowedBundle(bookId: Long): BorrowedBundle

    @Transaction
    @Query("SELECT * FROM borrowed_book")
    fun getBorrowedBundles(): Flow<List<BorrowedBundle>>
}