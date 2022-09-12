package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.BookPlacement
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Note

@Dao
interface BookPlacementDao {
    @Insert
    suspend fun insert(bookPlacement: BookPlacement)
}