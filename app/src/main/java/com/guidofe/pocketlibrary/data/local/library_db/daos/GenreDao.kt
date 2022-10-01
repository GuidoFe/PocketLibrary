package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Genre

@Dao
interface GenreDao {
    @Insert
    suspend fun insertAll(genres: List<Genre>): List<Long>

    //TODO: manage string case in query
    @Query("SELECT * FROM genre WHERE name IN (:names)")
    suspend fun getGenresByNames(names: List<String>): List<Genre>
}