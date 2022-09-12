package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Genre

@Dao
interface GenreDao {
    @Insert
    suspend fun insertAll(vararg genre: Genre): List<Long>
}