package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Transaction
    suspend fun upsert(note: Note) {
        val id = this.insert(note)
        if (id < 0)
            this.update(note)
    }

    @Delete
    suspend fun delete(note: Note)


}