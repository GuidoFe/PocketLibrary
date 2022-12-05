package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Genre

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(genres: List<Genre>): List<Long>

    // TODO: manage string case in query
    @Query("SELECT * FROM genre WHERE lower(name) IN ( :namesLowerCase )")
    suspend fun getGenresByNames(namesLowerCase: List<String>): List<Genre>

    @Query("SELECT * FROM genre WHERE lower(english_name) IN ( :namesLowerCase )")
    suspend fun getGenresByEnglishNames(namesLowerCase: List<String>): List<Genre>

    @Query("SELECT * FROM genre WHERE lower(name) LIKE lower(:start) || '%'")
    suspend fun getFromStartingLetters(start: String): List<Genre>

    @Query("SELECT * FROM genre")
    suspend fun getAll(): List<Genre>

    @Query("SELECT * FROM genre WHERE lang != :languageCode")
    suspend fun getGenresOfDifferentLanguage(languageCode: String): List<Genre>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateAll(genres: List<Genre>)
}