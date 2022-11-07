package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.utils.FormattingUtils

@Composable
fun DetailsTab(
    modifier: Modifier = Modifier,
    data: ViewBookImmutableData?
) {
    val boxPadding = 10.dp
    val gap = 5.dp
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.domain_24px),
                    null
                )
            },
            title = stringResource(R.string.publisher),
            text = data?.publisher ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.calendar_month_24px),
                    null
                )
            },
            title = stringResource(R.string.published_year),
            text = (data?.publishedYear?.toString()) ?: "?",
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.barcode_24px),
                    null
                )
            },
            title = stringResource(R.string.isbn),
            text = data?.identifier ?: "?",
            boxPadding = boxPadding,
            gap = gap,
            selectable = true
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.reader_mode_48px),
                    null
                )
            },
            title = stringResource(R.string.media_type),
            text = FormattingUtils.bookMediaToString(
                data?.media ?: Media.BOOK
            ),
            boxPadding = boxPadding,
            gap = gap
        )
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.language_48px),
                    null
                )
            },
            title = stringResource(R.string.language),
            text = data?.language ?: "?",
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
            DetailsTab(data = null)
        }
    }
}