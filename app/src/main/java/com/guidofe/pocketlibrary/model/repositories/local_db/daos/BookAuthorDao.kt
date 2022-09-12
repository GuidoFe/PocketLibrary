package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.BookAuthor

@Dao
interface BookAuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg bookAuthors: BookAuthor)

    @Insert
    suspend fun insert(bookAuthor: BookAuthor)

    @Delete
    suspend fun delete(bookAuthor:BookAuthor)
}