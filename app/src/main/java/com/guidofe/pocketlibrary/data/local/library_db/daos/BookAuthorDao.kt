package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.BookAuthor

@Dao
interface BookAuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(bookAuthors: List<BookAuthor>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookAuthor: BookAuthor)

    @Delete
    suspend fun delete(bookAuthor: BookAuthor)

    @Query("SELECT * FROM bookauthor WHERE bookId = :bookId ORDER BY position")
    suspend fun getBookAuthorSorted(bookId: Long): List<BookAuthor>
}