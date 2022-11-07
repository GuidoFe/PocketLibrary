package com.guidofe.pocketlibrary.data.local.library_db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import kotlinx.parcelize.Parcelize

@Parcelize
data class BorrowedBundle(
    @Embedded val info: BorrowedBook,
    @Relation(
        entity = Book::class,
        parentColumn = "bookId",
        entityColumn = "bookId"
    )
    val bookBundle: BookBundle,
) : Parcelable