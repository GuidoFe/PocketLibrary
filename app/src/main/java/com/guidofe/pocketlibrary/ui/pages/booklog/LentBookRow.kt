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
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.modules.SelectableBookCover
import com.guidofe.pocketlibrary.ui.utils.BookRowDefaults
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun LentBookRow(
    item: SelectableListItem<LibraryBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onBorrowerTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    onRowLongPress: (DpOffset) -> Unit = {},
    areButtonsActive: Boolean = true,
    dropdownMenu: @Composable () -> Unit = {}
) {
    val bookBundle = remember { item.value.bookBundle }
    val density = LocalDensity.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
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
                progress = item.value.bookBundle.progress?.phase
            )
            Spacer(Modifier.width(BookRowDefaults.coverTextDistance))
            Box(
                modifier = Modifier
                    .weight(1f, true)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(3f)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = { offset ->
                                            onRowLongPress(
                                                getMenuOffset(offset, density)
                                            )
                                        },
                                        onTap = onRowTap
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
                                text = bookBundle.authors
                                    .joinToString(", ") { it.name },
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
                                            onTap = { onBorrowerTap() }
                                        )
                                    }
                            ) {
                                Text(
                                    stringResource(R.string.lent_to_colon),
                                    style = BookRowDefaults.buttonLabelStyle,
                                )
                                Text(
                                    item.value.lent?.who ?: "???",
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
                                    style = BookRowDefaults.buttonLabelStyle,
                                )
                                Text(
                                    item.value.lent?.start?.let {
                                        ZonedDateTime.ofInstant(it, ZoneId.systemDefault())
                                            .toLocalDate().toString()
                                    } ?: "-",
                                    style = BookRowDefaults.buttonTextStyle,
                                )
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
@Preview(device = Devices.PIXEL_4)
private fun LibraryListRowPreview() {
    PreviewUtils.ThemeColumn(padding = 0.dp) {
        LentBookRow(
            item = SelectableListItem(
                PreviewUtils.exampleLibraryBundle.copy(
                    lent = LentBook(1, "Pinco", Instant.now())
                )
            ),
        )
    }
}