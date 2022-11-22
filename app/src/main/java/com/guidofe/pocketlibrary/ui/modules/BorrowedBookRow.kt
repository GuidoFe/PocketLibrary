package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.launch
import java.sql.Date

@Composable
fun BorrowedBookRow(
    item: SelectableListItem<BorrowedBundle>,
    swipeThreshold: Dp,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onLenderTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    onReturnByTap: () -> Unit = {},
    areButtonsActive: Boolean = true,
    onSwiped: () -> Unit = {},

) {
    val xOffset = remember { Animatable(0f) }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
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

    Box(modifier = modifier.background(MaterialTheme.colorScheme.tertiary)) {
        Icon(
            painterResource(R.drawable.archive_24px),
            stringResource(R.string.mark_as_returned),
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterEnd)
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.offset(
                with(density) { xOffset.value.toDp() },
                0.dp
            )
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
                                .weight(3f)
                                .padding(5.dp, 0.dp)
                        ) {
                            BoxWithConstraints {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = {},
                                                onDragEnd = {
                                                    with(density) {
                                                        if (
                                                            xOffset.value < (-swipeThreshold).toPx()
                                                        ) {
                                                            onSwiped()
                                                        }
                                                        coroutineScope.launch {
                                                            xOffset.animateTo(0f)
                                                        }
                                                    }
                                                },
                                                onDrag = { pointer, _ ->
                                                    var newValue = xOffset.value +
                                                        pointer.positionChange().x
                                                    if (newValue > 0f) newValue = 0f
                                                    coroutineScope.launch {
                                                        xOffset.snapTo(newValue)
                                                    }
                                                },
                                                onDragCancel = {
                                                    coroutineScope.launch {
                                                        xOffset.snapTo(0f)
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    Text(
                                        text = bookBundle.book.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 5.em,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = bookBundle.authors.joinToString(", ") {
                                            it.name
                                        },
                                        fontStyle = FontStyle.Italic,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1,
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
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(areButtonsActive) { onLenderTap() }
                                )
                                Text(
                                    startBuilder.toAnnotatedString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(areButtonsActive) { onStartTap() }
                                )
                                Text(
                                    returnByBuilder.toAnnotatedString(),
                                    style = MaterialTheme.typography.labelSmall,
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
            swipeThreshold = 10.dp
        )
    }
}