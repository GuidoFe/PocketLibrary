package com.guidofe.pocketlibrary.data.local.library_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookBundle (
    @Embedded val book: Book,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "authorId",
        associateBy = Junction(BookAuthor::class)
    )
    val authors: List<Author> = listOf(),
    @Relation(
        parentColumn = "bookId",
        entityColumn = "genreId",
        associateBy = Junction(BookGenre::class)
    )
    val genres: List<Genre> = listOf(),
    @Relation(
        parentColumn = "bookId",
        entityColumn = "placeId",
        associateBy = Junction(BookPlacement::class)
    )
    val place: Place? = null,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "roomId",
        associateBy = Junction(BookPlacement::class)
    )
    val room: Room? = null,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookshelfId",
        associateBy = Junction(BookPlacement::class)
    )
    val bookshelf: Bookshelf? = null,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val note: Note? = null,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId",
    )
    val loan: Loan? = null
): Parcelable {
    companion object {
        val EMPTY = BookBundle(book = Book(-1, ""))
    }
}