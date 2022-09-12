package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book

@Dao
interface BookDao {
    @Insert
    suspend fun insertAll(vararg books: Book)

    @Insert
    suspend fun insert(book: Book): Long

    @Delete
    suspend fun delete(book:Book)

    @Query("SELECT * FROM book")
    suspend fun getAll(): List<Book>

    @Query("select * FROM Author WHERE authorId IN (SELECT authorId FROM bookAuthor WHERE bookId = :bookId)")
    suspend fun getBookAuthors(bookId: Int): List<Author>
}