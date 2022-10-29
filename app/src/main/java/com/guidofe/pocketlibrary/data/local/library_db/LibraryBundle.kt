package com.guidofe.pocketlibrary.data.local.library_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class LibraryBundle (
    @Embedded val libraryInfo: LibraryBook,
    @Relation(
        entity = Book::class,
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val bookBundle: BookBundle,
): Parcelable