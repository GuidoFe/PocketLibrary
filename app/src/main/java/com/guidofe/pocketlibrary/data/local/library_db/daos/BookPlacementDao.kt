package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookPlacement
import com.guidofe.pocketlibrary.data.local.library_db.entities.Note

@Dao
interface BookPlacementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookPlacement: BookPlacement): Long

    @Update
    suspend fun update(bookPlacement: BookPlacement)
    @Query("DELETE from BookPlacement WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Transaction
    suspend fun upsert(placement: BookPlacement) {
        val id = this.insert(placement)
        if (id < 0)
            this.update(placement)
    }
}