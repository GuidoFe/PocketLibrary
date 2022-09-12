package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle

@Dao
interface BookBundleDao {
    @Transaction
    @Query("SELECT * FROM book WHERE bookId = :bookId;")
    suspend fun getBookBundle(bookId: Long): BookBundle

    //@Transaction
    //@Insert
    //suspend fun insertBookBundle(bundle: BookBundle)
}