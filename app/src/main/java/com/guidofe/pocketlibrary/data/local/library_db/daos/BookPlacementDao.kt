package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookPlacement

@Dao
interface BookPlacementDao {
    @Insert
    suspend fun insert(bookPlacement: BookPlacement)
}