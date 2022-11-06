package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.*
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import kotlinx.coroutines.flow.Flow

@Dao
interface LentBookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(lentBook: LentBook)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lentBooks: List<LentBook>)

    @Update
    suspend fun update(lentBook: LentBook)

    @Delete
    suspend fun delete(lentBook: LentBook)

    @Delete
    suspend fun delete(lentBooks: List<LentBook>)
}