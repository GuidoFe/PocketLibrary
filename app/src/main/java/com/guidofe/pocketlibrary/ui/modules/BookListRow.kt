package com.guidofe.pocketlibrary.ui.modules

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date

@Composable
fun LibraryListRow(
    item: SelectableListItem<BookBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
    GenericListRow(
        item.value.book.title,
        item.value.authors.joinToString(", ") { it.name },
        modifier,
        item.value.book.subtitle,
        item.value.book.coverURI,
        item.value.loan,
        item.value.book.isFavorite,
        item.isSelected,
        onRowTap,
        onRowLongPress,
        onCoverLongPress
    )
}

@Composable
fun ImportedBookListRow(
    item: SelectableListItem<ImportedBookData>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
) {
    GenericListRow(
        item.value.title,
        item.value.authors.joinToString(", "),
        modifier,
        item.value.subtitle,
        item.value.coverUrl?.let {
            Uri.parse(item.value.coverUrl)
        },
        loan = null,
        isFavorite = false,
        isSelected = item.isSelected,
        onRowTap = onRowTap,
        onRowLongPress = onRowLongPress,
        onCoverLongPress = onCoverLongPress
    )
}
private enum class CoverStatus {LOADED, LOADING, ERROR, EMPTY}

@Composable
private fun GenericListRow(
    title: String,
    authors: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    coverURI: Uri? = null,
    loan: Loan? = null,
    isFavorite: Boolean = false,
    isSelected: Boolean = false,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
    LaunchedEffect(isSelected) {
        Log.d("debug", "isSelected changed")
    }
    val selectionOffset: Dp by animateDpAsState(
        if (isSelected) (-5).dp else 0.dp,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
    )
    var coverStatus: CoverStatus by remember{mutableStateOf(CoverStatus.LOADING)}
    BoxWithConstraints(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(100.dp)
                    .width(maxWidth)
                    .padding(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(67.dp)
                        .height(100.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.shapes.medium
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(
                                x = selectionOffset,
                                y = selectionOffset,
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (coverURI == null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        3.dp,
                                        MaterialTheme.colorScheme.outline,
                                        MaterialTheme.shapes.medium
                                    )
                            ) {
                                Text(
                                    "NO COVER",
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    softWrap = true
                                )
                            }

                        } else {
                            AsyncImage(
                                model = coverURI,
                                contentDescription = stringResource(id = R.string.cover),
                                contentScale = ContentScale.FillBounds,
                                onLoading = { coverStatus = CoverStatus.LOADING },
                                onSuccess = { coverStatus = CoverStatus.LOADED },
                                onError = { coverStatus = CoverStatus.ERROR },
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                            if (coverStatus == CoverStatus.LOADING) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else if (coverStatus == CoverStatus.ERROR) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Icon(
                                        painterResource(R.drawable.error_24px),
                                        stringResource(R.string.error)
                                    )
                                    Text(stringResource(R.string.error_sadface))
                                }
                            }
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.hsl(0f, 0f, 0f, 0.6f))
                            ) {
                                Icon(
                                    painterResource(R.drawable.check_24px),
                                    stringResource(R.string.selected),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .scale(2f)
                                )
                            }
                        }
                    }
                    var statusBadge: Int? = null
                    if (loan != null)
                        statusBadge = when (loan.type) {
                            LoanType.LENT -> R.drawable.lent_24px
                            LoanType.BORROWED -> R.drawable.borrowed_24px
                        }
                    if (statusBadge != null)
                        BadgeIcon(painter = painterResource(statusBadge),
                            stringResource(R.string.lent),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(3.dp, 3.dp)
                        )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = onCoverLongPress,
                                    onTap = onRowTap
                                )
                            }
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 5.em,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            if (subtitle != null)
                                Text(
                                    text = subtitle,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )
                            Text(
                                text = authors,
                                fontStyle = FontStyle.Italic,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                        if (isFavorite) {
                            Icon(
                                painter = painterResource(R.drawable.star_filled_24px),
                                contentDescription = stringResource(R.string.favorite),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = onRowLongPress,
                                    onTap = onRowTap
                                )
                            }
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
private fun LibraryListRowPreview() {
    PocketLibraryTheme(darkTheme = true) {
        LibraryListRow(
            item = SelectableListItem(
                BookBundle(book, authors, genres, place, room, bookshelf, note, Loan(1, LoanType.BORROWED, "", Date(0L))),
                true
            )
        )
    }
}