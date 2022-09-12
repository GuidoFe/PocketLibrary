package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class RoomWithBookshelves (
        @Embedded val room: Room,
        @Relation(
                parentColumn = "roomId",
                entityColumn = "parent_room"
        )
        val bookshelves: List<Bookshelf>
)