package com.guidofe.pocketlibrary.data.local.library_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibraryBundle (
    @Embedded val info: LibraryBook,
    @Relation(
        entity = Book::class,
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val bookBundle: BookBundle,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val lent: LentBook? = null
): Parcelable