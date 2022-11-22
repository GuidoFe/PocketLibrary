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
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import kotlinx.coroutines.launch
import java.sql.Date
import java.time.Instant

@Composable
fun LentBookRow(
    item: SelectableListItem<LibraryBundle>,
    swipeThreshold: Dp,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onBorrowerTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    areButtonsActive: Boolean = true,
    onSwiped: () -> Unit = {},
) {
    val xOffset = remember { Animatable(0f) }
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val bookBundle = item.value.bookBundle
    val lentInfo = item.value.lent
    val lenderString = stringResource(R.string.lent_to_colon)
    val lenderBuilder = AnnotatedString.Builder(
        lenderString + "\n" + (lentInfo?.who ?: "???")
    )
    lenderBuilder.addStyle(
        SpanStyle(fontWeight = FontWeight.Bold), 0, lenderString.length
    )
    val startString = stringResource(R.string.start_colon)
    val startBuilder = AnnotatedString.Builder(
        startString + "\n" + (lentInfo?.start ?: "???")
    )
    startBuilder.addStyle(
        SpanStyle(fontWeight = FontWeight.Bold), 0, startString.length
    )

    Box(modifier = modifier.background(MaterialTheme.colorScheme.tertiary)) {
        Icon(
            painterResource(R.drawable.return_book_24px),
            stringResource(R.string.mark_as_returned),
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier.padding(20.dp).align(Alignment.CenterEnd)
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
                                        text = bookBundle.authors.joinToString(", ") { it.name },
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
                                        .clickable(areButtonsActive) { onBorrowerTap() }
                                )
                                Text(
                                    startBuilder.toAnnotatedString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(areButtonsActive) { onStartTap() }
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
        LentBookRow(
            item = SelectableListItem(
                PreviewUtils.exampleLibraryBundle.copy(
                    lent = LentBook(
                        1, "Pinco",
                        Date.from(Instant.now()) as Date
                    )
                )
            ),
            swipeThreshold = 10.dp
        )
    }
}