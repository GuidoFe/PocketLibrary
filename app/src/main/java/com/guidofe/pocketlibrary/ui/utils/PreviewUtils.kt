package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import java.sql.Date

class PreviewUtils {
    companion object {
        val exampleBookBundle = BookBundle(
            Book(
                bookId = 1,
                title = "The Lord of The Rings",
                subtitle = "The best story ever written",
                description = "A great story about hobbits",
                publisher = "Penguin",
                published = 1934,
                coverURI = null,
                identifier = "3245235423"
            ),
            listOf(Author(1, "J.R.R Tolkien"), Author(2, "Lewis")),
            genres = listOf(
                Genre(1, "Fantasy", "Fantasy", "en"),
                Genre(2, "Adventure", "Adventure", "en")
            ),
            note = Note(1, "It's a very good book")
        )

        val exampleLibraryBook = LibraryBook(
            1L,
            false,
        )

        val exampleLibraryBundle = LibraryBundle(
            exampleLibraryBook,
            exampleBookBundle,
            LentBook(1, "Mario", Date(System.currentTimeMillis()))
        )
        val exampleImportedBook = ImportedBookData(
            externalId = "id-234r2",
            title = "Very Interesting Book",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed " +
                "do eiusmod tempor incididunt ut labore et dolore magna aliqua. Suspendisse " +
                "sed nisi lacus sed. Risus in hendrerit gravida rutrum quisque. Vulputate " +
                "enim nulla aliquet porttitor lacus luctus accumsan tortor. Facilisis magna " +
                "etiam tempor orci eu lobortis elementum nibh tellus.",
            subtitle = "Short subtitle",
            publisher = "Adelphi",
            published = 1998,
            coverUrl = "https://m.media-amazon.com/images/I/51tAwFZt2XL.jpg",
            identifier = "987-9999999999",
            language = "en",
            authors = listOf("Pinco Pallino", "Giulio Cesare"),
            genres = listOf("Fantasy", "Humor")
        )

        @Composable
        fun ThemeRow(
            modifier: Modifier = Modifier,
            padding: Dp = 8.dp,
            content: @Composable () -> Unit
        ) {
            Row(modifier = modifier) {
                PocketLibraryTheme(darkTheme = false) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
                PocketLibraryTheme(darkTheme = true) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
            }
        }

        @Composable
        fun ThemeColumn(
            modifier: Modifier = Modifier,
            padding: Dp = 8.dp,
            content: @Composable () -> Unit
        ) {
            Column(modifier = modifier) {
                PocketLibraryTheme(darkTheme = false) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
                PocketLibraryTheme(darkTheme = true) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(padding)
                    ) {
                        content()
                    }
                }
            }
        }
    }
}