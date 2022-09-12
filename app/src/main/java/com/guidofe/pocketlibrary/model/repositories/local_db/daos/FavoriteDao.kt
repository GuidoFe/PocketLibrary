package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Favorite

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insert(favorite: Favorite)
}