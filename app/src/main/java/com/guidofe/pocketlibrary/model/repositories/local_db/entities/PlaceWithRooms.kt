package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PlaceWithRooms (
        @Embedded val place: Place,
        @Relation(
                parentColumn = "placeId",
                entityColumn = "parent_place"
        )
        val rooms: List<Room>
)