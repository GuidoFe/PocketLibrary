package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
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
    @Query("SELECT * FROM book WHERE identifier = :isbn")
    suspend fun getBookBundlesWithSameIsbn(isbn: String): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE title = :title")
    suspend fun getBookBundlesWithSameTitle(title: String): List<BookBundle>

    @Transaction
    @Query("SELECT * FROM book WHERE ( title = :title AND identifier = :isbn )")
    suspend fun getBookBundlesWithSameIsbnAndTitle(isbn: String, title: String): List<BookBundle>

    //@Transaction
    //@Insert
    //suspend fun insertBookBundle(bundle: BookBundle)
}