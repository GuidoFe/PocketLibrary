package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Author
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg books: Book)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM book")
    suspend fun getAll(): List<Book>

    @Query("select * FROM Author WHERE authorId IN (SELECT authorId FROM bookAuthor WHERE bookId = :bookId)")
    suspend fun getBookAuthors(bookId: Int): List<Author>
}