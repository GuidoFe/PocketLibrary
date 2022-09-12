package com.guidofe.pocketlibrary.model.repositories.local_db.daos

import androidx.room.Dao
import androidx.room.Insert
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Wishlist

@Dao
interface WishlistDao {
    @Insert
    suspend fun insert(wishlist: Wishlist)
}