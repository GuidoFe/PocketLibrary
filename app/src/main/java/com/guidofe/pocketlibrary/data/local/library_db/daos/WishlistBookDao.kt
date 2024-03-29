package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.WishlistBook

@Dao
interface WishlistBookDao {
    @Insert
    suspend fun insert(wishlist: WishlistBook)

    @Insert
    suspend fun insertAll(wishlists: List<WishlistBook>)

    @Query("SELECT * FROM wishlist_book")
    suspend fun getWishlistBooks(): List<WishlistBook>

    @Query("SELECT book.* FROM wishlist_book NATURAL JOIN book WHERE book.identifier = :isbn")
    suspend fun getBooksInWishlistWithSameIsbn(isbn: String): List<Book>

    @Query(
        "SELECT book.* FROM wishlist_book NATURAL JOIN book " +
            "WHERE book.identifier IN ( :isbnList )"
    )
    suspend fun getBooksInWishlistWithSameIsbns(isbnList: List<String>): List<Book>

    @Query("DELETE FROM wishlist_book WHERE bookId = :bookId")
    suspend fun delete(bookId: Long)

    @Query("DELETE FROM wishlist_book WHERE bookId IN ( :bookIds )")
    suspend fun deleteAll(bookIds: List<Long>)

    @Query("SELECT COUNT(bookId) FROM wishlist_book")
    suspend fun countBooksInWishlist(): Int
}