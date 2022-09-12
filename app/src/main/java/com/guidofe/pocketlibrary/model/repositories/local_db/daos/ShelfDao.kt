package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Shelf

@Dao
interface ShelfDao {
    @Insert
    suspend fun insert(shelf: Shelf): Long
}