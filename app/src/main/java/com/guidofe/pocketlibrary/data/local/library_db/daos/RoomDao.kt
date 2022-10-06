package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Room

@Dao
interface RoomDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(room: Room): Long

    @Query("SELECT roomId FROM room WHERE UPPER(name) LIKE UPPER(:name) AND parentPlace = :placeId")
    suspend fun getRoomIdByNameAndPlaceId(name: String, placeId: Long): Long?

    @Query("SELECT * FROM room WHERE parentPlace = :placeId")
    suspend fun getRoomsByParentPlaceId(placeId: Long): List<Room>
}