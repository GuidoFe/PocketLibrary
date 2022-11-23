package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import kotlinx.coroutines.flow.Flow

@Dao
interface LentBookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lentBook: LentBook)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lentBooks: List<LentBook>)

    @Update
    suspend fun update(lentBook: LentBook)

    @Update
    suspend fun updateAll(lentBooks: List<LentBook>)

    @Delete
    suspend fun delete(lentBook: LentBook)

    @Delete
    suspend fun delete(lentBooks: List<LentBook>)

    @Query("SELECT COUNT(bookId) FROM lent_book")
    fun countLentBooks(): Flow<Int>
}