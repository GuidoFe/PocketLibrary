package com.guidofe.pocketlibrary.ui.modules

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem

@Composable
fun LibraryListRow(
    item: SelectableListItem<LibraryBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
    val bundle = item.value.bookBundle
    GenericListRow(
        bundle.book.title,
        bundle.authors.joinToString(", ") { it.name },
        modifier,
        bundle.book.subtitle,
        bundle.book.coverURI,
        item.value.info.isFavorite,
        item.isSelected,
        item.value.lent != null,
        onRowTap,
        onRowLongPress,
        onCoverLongPress
    )
}

@Composable
fun WishlistRow(
    item: SelectableListItem<WishlistBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
    val bundle = item.value.bookBundle
    GenericListRow(
        bundle.book.title,
        bundle.authors.joinToString(", ") { it.name },
        modifier,
        bundle.book.subtitle,
        bundle.book.coverURI,
        false,
        item.isSelected,
        false,
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
        isFavorite = false,
        isSelected = item.isSelected,
        onRowTap = onRowTap,
        onRowLongPress = onRowLongPress,
        onCoverLongPress = onCoverLongPress
    )
}

@Composable
private fun GenericListRow(
    title: String,
    authors: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    coverURI: Uri? = null,
    isFavorite: Boolean = false,
    isSelected: Boolean = false,
    isLent: Boolean = false,
    onRowTap: (Offset) -> Unit = {},
    onRowLongPress: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {}
) {
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
                    coverURI,
                    isSelected,
                    onRowTap,
                    onCoverLongPress,
                    isLent
                )
                Box(
                    modifier = Modifier
                        .weight(1f, true)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp, 0.dp)
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
                                painter = painterResource(R.drawable.heart_filled_24px),
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

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
private fun LibraryListRowPreview() {
    PocketLibraryTheme(darkTheme = true) {
        LibraryListRow(
            item = SelectableListItem(
                PreviewUtils.exampleLibraryBundle
            )
        )
    }
}