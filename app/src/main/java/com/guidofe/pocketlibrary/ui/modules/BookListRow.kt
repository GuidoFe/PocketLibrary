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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.BookRowDefaults
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
        onCoverLongPress,
        item.value.bookBundle.progress?.phase
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
        onCoverLongPress = onCoverLongPress,
        enableCoverDiskCache = false
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
    onCoverLongPress: (Offset) -> Unit = {},
    progress: ProgressPhase? = null,
    enableCoverDiskCache: Boolean = true
) {
    var tapZoneOffset: Offset by remember { mutableStateOf(Offset.Zero) }
    BoxWithConstraints(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(115.dp)
                        .width(maxWidth)
                        .padding(
                            BookRowDefaults.horizontalPadding,
                            BookRowDefaults.verticalPadding
                        )
                ) {
                    SelectableBookCover(
                        coverURI,
                        isSelected,
                        onRowTap,
                        onCoverLongPress,
                        isLent,
                        progress,
                        enableDiskCache = enableCoverDiskCache
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f, true)
                            .onGloballyPositioned {
                                tapZoneOffset = it.positionInParent()
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        onRowLongPress(
                                            Offset(
                                                it.x + tapZoneOffset.x,
                                                it.y + tapZoneOffset.y
                                            )
                                        )
                                    },
                                    onTap = onRowTap
                                )
                            }
                    ) {
                        Spacer(Modifier.width(BookRowDefaults.coverTextDistance))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = title,
                                style = BookRowDefaults.titleStyle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            if (subtitle != null)
                                Text(
                                    text = subtitle,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    style = BookRowDefaults.subtitleStyle
                                )
                            Text(
                                text = authors,
                                style = BookRowDefaults.authorStyle,
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
                }
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4)
private fun LibraryListRowPreview() {
    PreviewUtils.ThemeColumn {
        LibraryListRow(
            item = SelectableListItem(
                PreviewUtils.exampleLibraryBundle
            ),
        )
    }
}