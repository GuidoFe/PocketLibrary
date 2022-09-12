package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BookshelfWithShelves (
        @Embedded val bookshelf: Bookshelf,
        @Relation(
                parentColumn = "bookshelfId",
                entityColumn = "parent_bookshelf"
        )
        val shelves: List<Shelf>
)