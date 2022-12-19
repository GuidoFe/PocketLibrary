package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

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
    onNotificationIconClick: () -> Unit = {},
    dropdownMenu: @Composable () -> Unit = {},
) {
    val bookBundle = item.value.bookBundle
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
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
                            if (item.value.info.end != null && !item.value.info.isReturned) {
                                if (item.value.info.notificationTime == null) {
                                    IconButton(onClick = onNotificationIconClick) {
                                        Icon(
                                            painterResource(R.drawable.notifications_off_24px),
                                            stringResource(R.string.notification_disabled),
                                            tint = MaterialTheme.colorScheme.onSurface
                                                .copy(alpha = 0.38f)
                                        )
                                    }
                                } else {
                                    IconButton(onClick = onNotificationIconClick) {
                                        Icon(
                                            painterResource(R.drawable.notifications_24px),
                                            stringResource(R.string.notification_enabled),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
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
                                    ZonedDateTime.ofInstant(
                                        item.value.info.start, ZoneId.systemDefault()
                                    ).toLocalDate().toString(),
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
                                    item.value.info.end?.toString() ?: "-",
                                    style = BookRowDefaults.buttonTextStyle,
                                    color = endDateColor(item.value.info)
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
private fun endDateColor(info: BorrowedBook): Color {
    return if (info.end != null && info.end <= LocalDate.now() && !info.isReturned)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.onSurface
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
                        Instant.now(),
                        LocalDate.parse("2021-12-25"),
                        isReturned = false,
                        notificationTime = null
                    ),
                    PreviewUtils.exampleBookBundle
                )
            ),
        )
    }
}