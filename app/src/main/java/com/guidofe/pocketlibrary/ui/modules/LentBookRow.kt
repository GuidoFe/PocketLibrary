package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
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
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date

@Composable
fun LentBookRow(
    item: SelectableListItem<LibraryBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onBorrowerTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    onRowLongPress: () -> Unit = {},
    areButtonsActive: Boolean = true,
) {
    val bookBundle = remember { item.value.bookBundle }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            SelectableBookCover(
                bookBundle.book.coverURI,
                item.isSelected,
                onRowTap,
                onCoverLongPress,
                progress = item.value.bookBundle.progress?.phase
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
                            .weight(3f)
                            .padding(5.dp, 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = bookBundle.book.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 5.em,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )
                                Text(
                                    text = bookBundle.authors
                                        .joinToString(", ") { it.name },
                                    fontStyle = FontStyle.Italic,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = { onRowLongPress() }
                                        )
                                    }
                            )
                        }
                        Divider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(areButtonsActive) { onBorrowerTap() }
                            ) {
                                Text(
                                    stringResource(R.string.lent_to_colon),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    item.value.lent?.who ?: "???",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(areButtonsActive) { onStartTap() }
                            ) {
                                Text(
                                    stringResource(R.string.start_colon),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    item.value.lent?.start?.toString() ?: "-",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
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
    PreviewUtils.ThemeColumn() {
        LentBookRow(
            item = SelectableListItem(
                PreviewUtils.exampleLibraryBundle.copy(
                    lent = LentBook(
                        1, "Pinco",
                        Date(System.currentTimeMillis())
                    )
                )
            ),
        )
    }
}