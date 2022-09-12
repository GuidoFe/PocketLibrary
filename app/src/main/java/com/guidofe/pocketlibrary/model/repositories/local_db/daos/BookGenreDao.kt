package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.BookAuthor
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.BookGenre

@Dao
interface BookGenreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg bookGenres: BookGenre)

    @Insert
    suspend fun insert(bookGenre: BookGenre)

    @Delete
    suspend fun delete(bookGenre:BookGenre)
}