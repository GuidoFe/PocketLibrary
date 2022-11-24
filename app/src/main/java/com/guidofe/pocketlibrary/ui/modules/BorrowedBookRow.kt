package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
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
    onRowLongPress: () -> Unit = {},
    areButtonsActive: Boolean = true,

) {
    val bookBundle = item.value.bookBundle
    val lenderString = stringResource(R.string.lender_colon)

    val lenderBuilder = AnnotatedString.Builder(
        lenderString + "\n" + (item.value.info.who ?: "???")
    )
    lenderBuilder.addStyle(
        SpanStyle(fontWeight = FontWeight.Bold), 0, lenderString.length
    )
    val startString = stringResource(R.string.start_colon)
    val startBuilder = AnnotatedString.Builder(
        startString + "\n" + (item.value.info.start)
    )
    startBuilder.addStyle(
        SpanStyle(fontWeight = FontWeight.Bold), 0, startString.length
    )
    val returnByString = stringResource(R.string.return_by_colon)
    val returnByBuilder = AnnotatedString.Builder(
        returnByString + "\n" + (item.value.info.end ?: "-")
    )
    returnByBuilder.addStyle(
        SpanStyle(fontWeight = FontWeight.Bold), 0, returnByString.length
    )
    val textColor = if (item.value.info.isReturned)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSurface
    Surface(
        color = if (item.value.info.isReturned)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
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
                colorFilter = if (item.value.info.isReturned)
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                else
                    null
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f, true)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onRowTap(it) },
                            onLongPress = { onRowLongPress() }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .padding(5.dp, 0.dp)
                ) {
                    BoxWithConstraints {
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
                                color = textColor
                            )
                            Text(
                                text = bookBundle.authors.joinToString(", ") {
                                    it.name
                                },
                                fontStyle = FontStyle.Italic,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                color = textColor
                            )
                        }
                    }
                    Divider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            lenderBuilder.toAnnotatedString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(areButtonsActive) { onLenderTap() }
                        )
                        Text(
                            startBuilder.toAnnotatedString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(areButtonsActive) { onStartTap() }
                        )
                        Text(
                            returnByBuilder.toAnnotatedString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable(areButtonsActive) { onReturnByTap() }
                        )
                    }
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
                        Date.valueOf("2022-12-25")
                    ),
                    PreviewUtils.exampleBookBundle
                )
            ),
        )
    }
}