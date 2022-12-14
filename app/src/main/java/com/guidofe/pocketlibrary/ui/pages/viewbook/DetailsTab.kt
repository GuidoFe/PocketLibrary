package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.ui.modules.RowWithIconAndSubtitle
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.languageName

@Composable
fun DetailsTab(
    modifier: Modifier = Modifier,
    book: Book?,
    isScrollable: Boolean
) {
    val boxPadding = 8.dp
    val gap = 4.dp
    val scrollState = rememberScrollState()
    Column(
        modifier = if (isScrollable)
            modifier.fillMaxWidth().verticalScroll(scrollState)
        else
            modifier.fillMaxWidth()
    ) {
        RowWithIconAndSubtitle(
            icon = {
                Icon(
                    painterResource(R.drawable.domain_24px),
                    null
                )
            },
            title = stringResource(R.string.publisher),
            text = book?.publisher ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIconAndSubtitle(
            icon = {
                Icon(
                    painterResource(R.drawable.calendar_month_24px),
                    null
                )
            },
            title = stringResource(R.string.published_year),
            text = (book?.published?.toString()) ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIconAndSubtitle(
            icon = {
                Icon(
                    painterResource(R.drawable.barcode_24px),
                    null
                )
            },
            title = stringResource(R.string.isbn),
            text = book?.identifier ?: "?",
            boxPadding = boxPadding,
            gap = gap,
            selectable = true
        )
        RowWithIconAndSubtitle(
            icon = {
                Icon(
                    painterResource(R.drawable.reader_mode_48px),
                    null
                )
            },
            title = stringResource(R.string.media_type),
            text = if (book?.isEbook == true)
                stringResource(R.string.ebook)
            else
                stringResource(R.string.book),
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIconAndSubtitle(
            icon = {
                Icon(
                    painterResource(R.drawable.language_48px),
                    null
                )
            },
            title = stringResource(R.string.language),
            text = languageName(book?.language),
            boxPadding = boxPadding,
            gap = gap
        )
    }
}

@Composable
@Preview
private fun DetailsTabPreview() {
    MaterialTheme {
        Surface {
            DetailsTab(
                book = PreviewUtils.exampleBookBundle.book,
                isScrollable = false
            )
        }
    }
}