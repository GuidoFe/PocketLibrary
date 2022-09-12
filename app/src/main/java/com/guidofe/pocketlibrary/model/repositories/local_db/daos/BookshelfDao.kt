package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Bookshelf

@Dao
interface BookshelfDao {
    @Insert
    suspend fun insert(bookshelf: Bookshelf): Long
}