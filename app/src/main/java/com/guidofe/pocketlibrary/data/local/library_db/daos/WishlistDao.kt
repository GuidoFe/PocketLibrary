package com.guidofe.pocketlibrary.data.local.library_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.data.local.library_db.entities.Wishlist

@Dao
interface WishlistDao {
    @Insert
    suspend fun insert(wishlist: Wishlist)
}