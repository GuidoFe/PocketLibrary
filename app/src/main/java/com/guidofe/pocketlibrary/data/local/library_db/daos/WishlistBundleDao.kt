package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle

@Dao
interface WishlistBundleDao {
    @Transaction
    @Query("SELECT * FROM wishlist_book WHERE bookId = :bookId;")
    suspend fun getWishlistBundle(bookId: Long): WishlistBundle

    @Transaction
    @Query("SELECT * FROM wishlist_book")
    fun getWishlistBundles(): PagingSource<Int, WishlistBundle>

    @Transaction
    @Query(
        "SELECT DISTINCT wishlist_book.* FROM wishlist_book NATURAL JOIN book NATURAL JOIN " +
            "bookauthor NATURAL JOIN Author WHERE lower( book.title ) LIKE " +
            "'%' || :lowerString || '%' OR lower( Author.name ) LIKE '%' || :lowerString || '%'"
    )
    fun getWishlistBundlesByString(lowerString: String): PagingSource<Int, WishlistBundle>
}