package com.guidofe.pocketlibrary.data.local.library_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.data.local.library_db.views.SortedBookAuthor
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookBundle(
    @Embedded val book: Book,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "authorId",
        associateBy = Junction(SortedBookAuthor::class)
    )
    val authors: List<Author> = emptyList(),
    @Relation(
        parentColumn = "bookId",
        entityColumn = "genreId",
        associateBy = Junction(BookGenre::class)
    )
    val genres: List<Genre> = emptyList(),
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val note: Note? = null,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val progress: Progress? = null,
) : Parcelable