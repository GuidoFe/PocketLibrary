package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import java.sql.Date

@Composable
fun LibraryListItem(
    bookBundle: BookBundle,
    onTap: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {}
) {
    BoxWithConstraints {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = onTap,
                    onLongPress = onLongPress
                )
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(100.dp)
                    .width(maxWidth)
                    .padding(5.dp)
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    ) {
                    AsyncImage(
                        model = bookBundle.book.coverURI,
                        contentDescription = stringResource(id = R.string.cover),
                        placeholder = painterResource(id = R.drawable.sample_cover),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .width((67).dp)
                            .height(100.dp)
                    )
                    var statusBadge: Int? = null
                    if (bookBundle.loan != null)
                        statusBadge = when (bookBundle.loan.type) {
                            LoanType.LENT -> R.drawable.ic_baseline_lent_24
                            LoanType.BORROWED -> R.drawable.ic_baseline_borrowed_24
                        }
                    if (statusBadge != null)
                        BadgeIcon(painter = painterResource(statusBadge),
                            stringResource(R.string.lent),
                            modifier = Modifier
                                .offset(3.dp, 3.dp)
                        )
                }
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f, true)
                ) {
                    Text(
                        text = bookBundle.book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 5.em,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    if (bookBundle.book.subtitle != null)
                        Text(
                            text = bookBundle.book.subtitle ?: "",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    Text(
                        text = bookBundle.authors.joinToString(", "),
                        fontStyle = FontStyle.Italic,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
                if (bookBundle.book.isFavorite) {
                    Image(
                        painter = painterResource(R.drawable.ic_baseline_star_24),
                        contentDescription = stringResource(R.string.favorite),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}

var book = Book(
    bookId = 1L,
    title = "Dune wqfqew qwfqw wfqwfqw wqefqewf wqfqfqwqewf qwfeqw qwfqw qwefqwf qfwfq",
    subtitle = "The Greatest Sci-Fi Story",
    description = "Opposing forces struggle for control of the universe when the archenemy of the cosmic emperor is banished to a barren world where savages fight for water",
    publisher = "Penguin",
    published = 1990,
    industryIdentifierType = IndustryIdentifierType.ISBN_13,
    identifier = "9780441172719",
    language = "en",
    isFavorite = true
)
var authors = listOf(Author(1L, "Frank Herbert"), Author(2L, "Princess Irulan"))
val genres: List<Genre> = listOf(Genre(1L, "Fantasy"), Genre(2L, "Sci-fi"))
var place = Place(1L, "Home")
var room = Room(1L, "Livingroom", 1L)
var bookshelf = Bookshelf(1L, "Big library", 1L)
var note =  Note(1L, "Very cool book")

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
fun LibraryListItemPreview() {
    PocketLibraryTheme(darkTheme = false) {
        LibraryListItem(bookBundle = BookBundle(book, authors, genres, place, room, bookshelf, note, Loan(1, LoanType.BORROWED, "", Date(0L))))
    }
}