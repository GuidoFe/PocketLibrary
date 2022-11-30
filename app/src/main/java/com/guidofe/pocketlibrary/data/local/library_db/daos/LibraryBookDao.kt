package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.LibraryBook
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase

@Dao
interface LibraryBookDao {
    @Insert
    suspend fun insert(libraryBook: LibraryBook)

    @Insert
    suspend fun insertAll(libraryBooks: List<LibraryBook>)

    @Query("SELECT book.* FROM library_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInLibraryWithSameIsbn(isbn: String): List<Book>

    @Query(
        "SELECT book.* FROM library_book NATURAL JOIN book " +
            "WHERE book.identifier IN ( :isbnList )"
    )
    suspend fun getBooksInLibraryWithSameIsbns(isbnList: List<String>): List<Book>

    @Query("UPDATE library_book SET isFavorite = :isFavorite WHERE bookId = :bookId")
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)

    @Query("UPDATE library_book SET isFavorite = :isFavorite WHERE bookId IN ( :bookIds )")
    suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean)

    @Query("SELECT COUNT(bookId) FROM library_book")
    suspend fun countBooksInLibrary(): Int

    @Query("SELECT COUNT(bookId) FROM library_book NATURAL JOIN progress WHERE phase = :phase")
    suspend fun countLibraryBooksAtProgressPhase(phase: ProgressPhase): Int

    @Query(
        "SELECT phase, COUNT(bookId) as total " +
            "FROM library_book NATURAL JOIN progress GROUP BY phase"
    )
    @MapInfo(keyColumn = "phase", valueColumn = "total")
    suspend fun countLibraryBooksAtEveryPhase(): Map<ProgressPhase, Int>
}