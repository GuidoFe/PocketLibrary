package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Note

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)
}