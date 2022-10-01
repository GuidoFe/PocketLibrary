package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle

@Dao
interface BookBundleDao {
    @Transaction
    @Query("SELECT * FROM book WHERE bookId = :bookId;")
    suspend fun getBookBundle(bookId: Long): BookBundle

    @Transaction
    @Query("SELECT * FROM book LIMIT :pageSize OFFSET :pageNumber * :pageSize")
    suspend fun getBookBundles(pageNumber: Int = 0, pageSize: Int): List<BookBundle>

    //@Transaction
    //@Insert
    //suspend fun insertBookBundle(bundle: BookBundle)
}