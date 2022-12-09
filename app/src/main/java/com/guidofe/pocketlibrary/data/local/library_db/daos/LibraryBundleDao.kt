package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
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
    @Query("SELECT * FROM library_book")
    fun getLibraryBundlesPagingSource(): PagingSource<Int, LibraryBundle>

    @Transaction
    @Query(
        "SELECT library_book.* FROM library_book NATURAL JOIN book " +
            "WHERE book.identifier IN ( :isbnList )"
    )
    suspend fun getLibraryBundlesWithSameIsbns(isbnList: List<String>): List<LibraryBundle>

    @Transaction
    @RawQuery(observedEntities = [LibraryBundle::class])
    fun getLibraryBundlesWithCustomQuery(
        query: SupportSQLiteQuery
    ): PagingSource<Int, LibraryBundle>

    @Transaction
    @Query(
        "SELECT DISTINCT library_book.* FROM lent_book NATURAL JOIN library_book " +
            "NATURAL JOIN book NATURAL JOIN bookauthor NATURAL JOIN Author WHERE " +
            "lower( book.title ) LIKE '%' || :lowerString || '%' OR lower( Author.name ) " +
            "LIKE '%' || :lowerString || '%'"
    )
    fun getLentBundlesByString(lowerString: String): Flow<List<LibraryBundle>>
}