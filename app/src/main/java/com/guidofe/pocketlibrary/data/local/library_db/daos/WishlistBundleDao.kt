package com.guidofe.pocketlibrary.data.local.library_db.daos

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
    suspend fun getWishlistBundles(): List<WishlistBundle>

    @Transaction
    @Query("SELECT * FROM wishlist_book LIMIT :offset,:limit")
    suspend fun getWishlistBundles(offset: Int, limit: Int): List<WishlistBundle>
}