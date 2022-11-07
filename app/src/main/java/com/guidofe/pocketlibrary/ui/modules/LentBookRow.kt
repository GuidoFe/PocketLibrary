package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date
import java.time.Instant

@Composable
fun LentBookRow(
    item: SelectableListItem<LibraryBundle>,
    modifier: Modifier = Modifier,
    onRowTap: (Offset) -> Unit = {},
    onMarkAsReturned: (LibraryBundle) -> Unit = {},
    onCoverLongPress: (Offset) -> Unit = {},
    onBorrowerTap: () -> Unit = {},
    onStartTap: () -> Unit = {},
    areButtonsActive: Boolean = true
) {
    val bookBundle = item.value.bookBundle
    val lentInfo = item.value.lent
    val lenderString = stringResource(R.string.lent_to_colon)
    val density = LocalDensity.current
    var isMenuOpen by remember { mutableStateOf(false) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
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

    Box(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
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
                                            detectTapGestures(onLongPress = {
                                                tapOffset = it
                                                isMenuOpen = true
                                            })
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
                                /*val (xDp, yDp) = with(density) {
                                    (tapOffset.x.toDp() - maxHeight) to (tapOffset.y.toDp())
                                }*/
                                DropdownMenu(
                                    expanded = isMenuOpen,
                                    onDismissRequest = { isMenuOpen = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.mark_as_returned)) },
                                        onClick = {
                                            isMenuOpen = false
                                            onMarkAsReturned(item.value)
                                        }
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
            )
        )
    }
}