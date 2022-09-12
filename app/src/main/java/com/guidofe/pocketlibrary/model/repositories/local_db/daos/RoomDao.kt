package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Room

@Dao
interface RoomDao {
    @Insert
    suspend fun insert(room: Room): Long
}