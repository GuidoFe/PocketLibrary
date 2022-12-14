package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.modules.EmptyBookCover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HorizontalCompactBookHeading(
    bundle: BookBundle,
    onHeadingClick: () -> Unit,
    onGenreClick: (String) -> Unit
) {
    val genreScrollState = rememberScrollState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            // .fillMaxWidth()
            .padding(8.dp)
        // .height()
    ) {
        Box(
            Modifier
                .height(125.dp)
        ) {
            val coverUri = bundle.book.coverURI
            if (coverUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUri)
                        .build(),
                    contentDescription = stringResource(id = R.string.cover),
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.fillMaxHeight()
                )
            } else {
                EmptyBookCover(
                    Modifier
                    // .fillMaxHeight()
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier.clickable { onHeadingClick() }
                ) {
                    Text(
                        bundle.book.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    bundle.book.subtitle?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val authorsString =
                        bundle.authors.joinToString(", ") { it.name }
                    Text(
                        authorsString,
                        style = MaterialTheme.typography.labelLarge,
                        fontStyle = FontStyle.Italic,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Light
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .horizontalScroll(genreScrollState)
                        .height(32.dp)
                ) {
                    bundle.genres.forEach {
                        SuggestionChip(
                            onClick = {
                                onGenreClick(it.name)
                            },
                            label = {
                                Text(
                                    it.name,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                        )
                    }
                }
                if (bundle.genres.any {
                    it.englishName != null && it.lang != "en"
                }
                ) {
                    Image(
                        painterResource(R.drawable.google_translate_attribution_2x),
                        stringResource(R.string.google_translate_attribution),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.padding(5.dp, 2.dp)
                    )
                }
            }
        }
    }
}