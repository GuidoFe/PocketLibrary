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

    @Delete
    suspend fun delete(books: List<Book>)

    @Query("DELETE FROM book WHERE bookId IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT * FROM book")
    suspend fun getAll(): List<Book>

    @Query("SELECT * FROM Author WHERE authorId IN (SELECT authorId FROM bookAuthor WHERE bookId = :bookId)")
    suspend fun getBookAuthors(bookId: Long): List<Author>

    @Query("UPDATE book SET isFavorite = :isFavorite WHERE bookId = :bookId")
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)

    @Query("UPDATE book SET isFavorite = :isFavorite WHERE bookId IN (:bookIds)")
    suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean)

}