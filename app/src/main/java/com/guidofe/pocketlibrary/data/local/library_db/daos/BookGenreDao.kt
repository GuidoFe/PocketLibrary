package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookGenre

@Dao
interface BookGenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(bookGenres: List<BookGenre>)

    @Insert
    suspend fun insert(bookGenre: BookGenre)

    @Delete
    suspend fun delete(bookGenre: BookGenre)

    @Query("DELETE FROM bookgenre WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)
}