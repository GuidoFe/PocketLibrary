package com.guidofe.pocketlibrary.ui.modules

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date

@Composable
private fun BorrowedBookRow(
    item: SelectableListItem<BorrowedBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
    val bookBundle = item.value.bookBundle
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
                SelectableBookCover(
                    bookBundle.book.coverURI,
                    item.isSelected,
                    onRowTap,
                    onCoverLongPress
                )
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
                                text = bookBundle.book.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 5.em,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                            )
                            Text(
                                text = bookBundle.authors.joinToString(", "){it.name},
                                fontStyle = FontStyle.Italic,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
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

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
private fun LibraryListRowPreview() {
    PocketLibraryTheme(darkTheme = true) {
        BorrowedBookRow(
            item = SelectableListItem(
                BorrowedBundle(
                    BorrowedBook(
                        1,
                        "Tim Minchin",
                        Date.valueOf("2022-03-11"),
                        Date.valueOf("2022-12-25")),
                    PreviewUtils.exampleBookBundle
                )
            )
        )
    }
}