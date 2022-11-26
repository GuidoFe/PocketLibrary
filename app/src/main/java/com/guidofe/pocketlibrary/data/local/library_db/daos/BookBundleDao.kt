package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import kotlinx.coroutines.flow.Flow

@Dao
interface BookBundleDao {
    @Transaction
    @Query("SELECT * FROM book WHERE bookId = :bookId;")
    suspend fun getBookBundle(bookId: Long): BookBundle

    @Transaction
    @Query("SELECT * FROM book")
    fun getBookBundles(): Flow<List<BookBundle>>

    @Transaction
    @Query("SELECT * FROM book LIMIT :offset,:limit")
    suspend fun getBookBundles(offset: Int, limit: Int): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE identifier = :isbn")
    suspend fun getBookBundlesWithSameIsbn(isbn: String): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE identifier IN ( :isbnList )")
    suspend fun getBookBundlesWithSameIsbns(isbnList: List<String>): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE LOWER(title) = LOWER(:title)")
    suspend fun getBookBundlesWithSameTitle(title: String): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE title IN ( :titles )")
    suspend fun getBookBundlesWithSimilarTitles(titles: List<String>): List<BookBundle>

    @Transaction
    @Query("SELECT book.* FROM book NATURAL JOIN progress WHERE progress.phase = :phase")
    suspend fun getBookBundlesAtProgressPhase(phase: ProgressPhase): List<BookBundle>

    // @Transaction
    // @Insert
    // suspend fun insertBookBundle(bundle: BookBundle)
}