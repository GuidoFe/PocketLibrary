package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookAuthor

@Dao
interface BookAuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(bookAuthors: List<BookAuthor>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookAuthor: BookAuthor)

    @Delete
    suspend fun delete(bookAuthor: BookAuthor)
}