package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(progress: Progress): Long

    @Update
    suspend fun update(progress: Progress)

    @Transaction
    suspend fun upsert(progress: Progress) {
        val id = this.insert(progress)
        if (id < 0)
            this.update(progress)
    }

    @Query("DELETE FROM progress WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("SELECT COUNT(bookId) FROM progress WHERE phase = :progress")
    fun countBooks(progress: ProgressPhase): Flow<Int>
}