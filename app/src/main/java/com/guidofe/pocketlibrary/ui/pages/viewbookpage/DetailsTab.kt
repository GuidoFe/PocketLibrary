package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.utils.FormattingUtils
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils

@Composable
fun DetailsTab(book: Book, modifier: Modifier = Modifier) {
    val boxPadding = 10.dp
    val gap = 5.dp
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.domain_48px),
                    null
                )},
            title = stringResource(R.string.publisher),
            text = book.publisher ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.calendar_month_48px),
                   null
                )},
            title = stringResource(R.string.published_year),
            text = (book.published?.toString()) ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = { Icon(
                painterResource(R.drawable.barcode_48px),
                   null
            )},
            title = stringResource(R.string.isbn), 
            text = book.identifier ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.reader_mode_48px),
                   null
                )},
            title = stringResource(R.string.media_type), 
            text = FormattingUtils.bookMediaToString(book.media),
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.language_48px),
                   null
                )},
            title = stringResource(R.string.language),
            text = book.language,
            boxPadding = boxPadding,
            gap = gap
        )
    }
}

@Composable
@Preview
fun DetailsTabPreview () {
    MaterialTheme {
        Surface {
            DetailsTab(book = PreviewUtils.exampleBookBundle.book)
        }
    }
}