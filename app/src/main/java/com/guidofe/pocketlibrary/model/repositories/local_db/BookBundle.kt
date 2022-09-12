package com.guidofe.pocketlibrary.model.repositories.local_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookBundle (
    @Embedded val book: Book,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "authorId",
        associateBy = Junction(BookAuthor::class)
    )
    val authors: List<Author>,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "genreId",
        associateBy = Junction(BookGenre::class)
    )
    val genres: List<Genre>,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "placeId",
        associateBy = Junction(BookPlacement::class)
    )
    val place: Place?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "roomId",
        associateBy = Junction(BookPlacement::class)
    )
    val room: Room?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookshelfId",
        associateBy = Junction(BookPlacement::class)
    )
    val bookshelf: Bookshelf?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "shelfId",
        associateBy = Junction(BookPlacement::class)
    )
    val shelf: Shelf?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val isFavorite: Favorite?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val note: Note?,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val inWishlist: Wishlist?,
    ): Parcelable