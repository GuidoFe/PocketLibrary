package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author

@Dao
interface AuthorDao {
    @Insert
    suspend fun insertAll(vararg authors: Author): List<Long>

    @Query("SELECT * FROM Author WHERE authorId = :id")
    suspend fun getAuthorById(id: Int): Author

    @Query("SELECT * FROM Author WHERE name IN (:authorsNames)")
    suspend fun getExistingAuthors(authorsNames: List<String>): List<Author>
}