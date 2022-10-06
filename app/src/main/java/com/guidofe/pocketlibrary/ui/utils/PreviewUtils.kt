package com.guidofe.pocketlibrary.ui.utils

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import kotlinx.coroutines.flow.MutableStateFlow

class PreviewUtils() {
    companion object {
        val exampleBookBundle = BookBundle(
            Book(
                bookId = 1,
                title = "The Lord of The Rings",
                subtitle = "The best story ever written",
                description = "A great story about hobbits",
                publisher = "Penguin",
                published = 1934,
                isOwned = true,
                isFavorite = false,
                progress = Progress.READ,
                coverURI = null,
                identifier = "3245235423"
            ),
            listOf(Author(1, "J.R.R Tolkien"), Author(2, "Lewis")),
            genres = listOf(
                Genre(1, "Fantasy"),
                Genre(2, "Adventure")
            ),
            place = Place(1, "Home"),
            room = Room(1, "Bedroom", 1),
            bookshelf = Bookshelf(1, "Bedside", 1),
            note = Note(1, "It's a very good book")
        )

        val fakeAppBarStateDelegate = AppBarStateDelegate(MutableStateFlow(AppBarState()))

    }
}