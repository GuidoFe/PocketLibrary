package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Shelf

@Dao
interface ShelfDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shelf: Shelf): Long

    @Update
    suspend fun update(shelf: Shelf)

    @Delete
    suspend fun delete(shelf: Shelf)

    @Query("SELECT * FROM shelf")
    suspend fun getAll()
}