package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Place

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: Place): Long

    @Query("SELECT placeId FROM place WHERE UPPER(name) LIKE UPPER(:name)")
    suspend fun getPlaceByName(name: String): Long?

    @Query("SELECT * FROM place")
    suspend fun getAllPlaces(): List<Place>
}