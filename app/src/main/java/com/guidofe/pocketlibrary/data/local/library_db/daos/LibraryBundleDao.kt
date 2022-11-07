package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryBundleDao {
    @Transaction
    @Query("SELECT * FROM library_book WHERE bookId = :bookId;")
    suspend fun getLibraryBundle(bookId: Long): LibraryBundle

    @Transaction
    @Query("SELECT * FROM library_book")
    fun getLibraryBundlesFlow(): Flow<List<LibraryBundle>>

    @Transaction
    @Query("SELECT library_book.* FROM library_book NATURAL JOIN lent_book")
    fun getLentLibraryBundlesFlow(): Flow<List<LibraryBundle>>

    @Transaction
    @Query("SELECT * FROM library_book")
    suspend fun getLibraryBundles(): List<LibraryBundle>

    @Transaction
    @Query("SELECT * FROM library_book LIMIT :offset,:limit")
    suspend fun getLibraryBundles(offset: Int, limit: Int): List<LibraryBundle>

    @Transaction
    @Query(
        "SELECT library_book.* FROM library_book NATURAL JOIN book " +
            "WHERE book.identifier IN ( :isbnList )"
    )
    suspend fun getLibraryBundlesWithSameIsbns(isbnList: List<String>): List<LibraryBundle>
}