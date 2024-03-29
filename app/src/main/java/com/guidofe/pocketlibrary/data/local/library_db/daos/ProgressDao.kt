package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(progress: Progress): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(progress: List<Progress>): List<Long>

    @Update
    suspend fun update(progress: Progress)

    @Update
    suspend fun updateAll(progress: List<Progress>)

    @Transaction
    suspend fun upsert(progress: Progress) {
        val id = this.insert(progress)
        if (id < 0)
            this.update(progress)
    }

    @Query("DELETE FROM progress WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("SELECT COUNT(bookId) FROM progress WHERE phase = :progress")
    suspend fun countBooks(progress: ProgressPhase): Int
}