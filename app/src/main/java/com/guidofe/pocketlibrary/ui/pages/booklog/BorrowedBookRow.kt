package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.ui.modules.SelectableBookCover
import com.guidofe.pocketlibrary.ui.utils.BookRowDefaults
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date

@Composable
fun BorrowedBookRow(
    item: SelectableListItem<BorrowedBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onLenderTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    onReturnByTap: () -> Unit = {},
    onRowLongPress: (DpOffset) -> Unit = {},
    areButtonsActive: Boolean = true,
    dropdownMenu: @Composable () -> Unit = {},
) {
    val bookBundle = remember { item.value.bookBundle }
    val density = LocalDensity.current
    Surface(
        color = if (item.value.info.isReturned)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
        modifier = modifier

    ) {
        Box(modifier = Modifier.height(120.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        BookRowDefaults.horizontalPadding,
                        BookRowDefaults.verticalPadding
                    )
            ) {
                SelectableBookCover(
                    bookBundle.book.coverURI,
                    item.isSelected,
                    onRowTap,
                    onCoverLongPress,
                    progress = item.value.bookBundle.progress?.phase,
                    colorFilter = remember {
                        if (item.value.info.isReturned)
                            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        else
                            null
                    }
                )
                Spacer(Modifier.width(BookRowDefaults.coverTextDistance))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, true)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = { onRowTap(it) },
                                        onLongPress = { offset ->
                                            onRowLongPress(
                                                getMenuOffset(offset, density)
                                            )
                                        }
                                    )
                                }
                        ) {
                            dropdownMenu()
                            Text(
                                text = bookBundle.book.title,
                                style = BookRowDefaults.titleStyle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            Text(
                                text = remember {
                                    bookBundle.authors.joinToString(", ") {
                                        it.name
                                    }
                                },
                                style = BookRowDefaults.authorStyle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { onLenderTap() }
                                        )
                                    }
                            ) {
                                Text(
                                    stringResource(R.string.lender_colon),
                                    maxLines = 1,
                                    style = BookRowDefaults.buttonLabelStyle
                                )
                                Text(
                                    item.value.info.who ?: "???",
                                    style = BookRowDefaults.buttonTextStyle,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { onStartTap() }
                                        )
                                    }
                            ) {
                                Text(
                                    stringResource(R.string.start_colon),
                                    maxLines = 1,
                                    style = BookRowDefaults.buttonLabelStyle
                                )
                                Text(
                                    remember { item.value.info.start.toString() },
                                    style = BookRowDefaults.buttonTextStyle
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = { onReturnByTap() }
                                        )
                                    }
                            ) {
                                Text(
                                    stringResource(R.string.return_by_colon),
                                    maxLines = 1,
                                    style = BookRowDefaults.buttonLabelStyle
                                )
                                Text(
                                    remember { item.value.info.end?.toString() ?: "-" },
                                    style = BookRowDefaults.buttonTextStyle
                                )
                            }
                        }
                    }
                }
            }
            if (!areButtonsActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onRowTap(it) },
                            )
                        }
                )
            }
        }
    }
}

private fun getMenuOffset(offset: Offset, density: Density): DpOffset {
    with(density) {
        return DpOffset(
            x = offset.x.toDp(),
            y = offset.y.toDp() - 68.5.dp
        )
    }
}

@Composable
fun ExtendedBorrowedBookRow(
    item: SelectableListItem<BorrowedBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onLenderTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    onReturnByTap: () -> Unit = {},
    onRowLongPress: () -> Unit = {},
    areButtonsActive: Boolean = true,

) {
    val bookBundle = remember { item.value.bookBundle }
    Surface(
        color = if (item.value.info.isReturned)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
        modifier = modifier

    ) {
        Box(modifier = Modifier.height(120.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        BookRowDefaults.horizontalPadding,
                        BookRowDefaults.verticalPadding
                    )
            ) {
                SelectableBookCover(
                    bookBundle.book.coverURI,
                    item.isSelected,
                    onRowTap,
                    onCoverLongPress,
                    progress = item.value.bookBundle.progress?.phase,
                    colorFilter = remember {
                        if (item.value.info.isReturned)
                            ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                        else
                            null
                    }
                )
                Spacer(Modifier.width(BookRowDefaults.coverTextDistance))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onRowTap(it) },
                                onLongPress = { onRowLongPress() }
                            )
                        }
                ) {
                    Text(
                        text = bookBundle.book.title,
                        style = BookRowDefaults.titleStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Text(
                        text = remember {
                            bookBundle.authors.joinToString(", ") {
                                it.name
                            }
                        },
                        style = BookRowDefaults.authorStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(
                            BookRowDefaults.extendedNameCellWidth
                        )
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onLenderTap() }
                            )
                        }
                ) {
                    Text(
                        item.value.info.who ?: "???",
                        style = BookRowDefaults.buttonTextStyle,
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(
                            BookRowDefaults.extendedDateCellWidth
                        )
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onStartTap() }
                            )
                        }
                ) {
                    Text(
                        remember { item.value.info.start.toString() },
                        style = BookRowDefaults.buttonTextStyle,
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(
                            BookRowDefaults.extendedDateCellWidth
                        )
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onReturnByTap() }
                            )
                        }
                ) {
                    Text(
                        remember { item.value.info.end?.toString() ?: "-" },
                        style = BookRowDefaults.buttonTextStyle,
                    )
                }
            }
            if (!areButtonsActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onRowTap(it) },
                            )
                        }
                )
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_4)
@Preview(widthDp = 600)
private fun BorrowedBookRowPreview() {
    PreviewUtils.ThemeColumn() {
        BorrowedBookRow(
            item = SelectableListItem(
                BorrowedBundle(
                    BorrowedBook(
                        1,
                        "Tim Minchin",
                        Date.valueOf("2022-03-11"),
                        Date.valueOf("2022-12-25"),
                        isReturned = false,
                        notificationTime = null
                    ),
                    PreviewUtils.exampleBookBundle
                )
            ),
        )
    }
}

@Composable
@Preview(widthDp = 840)
private fun ExtendedBorrowedBookRowPreview() {
    PreviewUtils.ThemeColumn() {
        ExtendedBorrowedBookRow(
            item = SelectableListItem(
                BorrowedBundle(
                    BorrowedBook(
                        1,
                        "Tim Minchin",
                        Date.valueOf("2022-03-11"),
                        Date.valueOf("2022-12-25"),
                        isReturned = false,
                        notificationTime = null
                    ),
                    PreviewUtils.exampleBookBundle
                )
            ),
        )
    }
}