package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Bookshelf

@Dao
interface BookshelfDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bookshelf: Bookshelf): Long

    @Query("SELECT bookshelfId FROM bookshelf WHERE UPPER(name) LIKE UPPER(:name) AND parentRoom = :roomId")
    suspend fun getBookshelfIdByNameAndRoomId(name: String, roomId: Long): Long?
}