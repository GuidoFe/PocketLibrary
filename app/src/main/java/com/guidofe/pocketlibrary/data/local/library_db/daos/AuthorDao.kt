package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Author

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(authors: List<Author>): List<Long>

    @Query("SELECT * FROM Author WHERE authorId = :id")
    suspend fun getAuthorById(id: Long): Author

    @Query("SELECT * FROM Author WHERE name IN (:authorsNames)")
    suspend fun getExistingAuthors(authorsNames: List<String>): List<Author>
}