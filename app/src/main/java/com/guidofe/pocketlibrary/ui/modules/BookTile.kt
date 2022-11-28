package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils

@Composable
fun BookTile(item: BookBundle, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() }
    ) {
        if (item.book.coverURI != null) {
            AsyncImage(
                model = item.book.coverURI,
                contentDescription = item.book.title,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxHeight()
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(0.66f)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(5.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Text(
                        item.book.title,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 3
                    )
                    Text(
                        item.authors.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun BookTilePreview() {
    Row {
        PocketLibraryTheme(darkTheme = false) {
            BookTile(
                PreviewUtils.exampleBookBundle,
            ) {}
        }
        Divider(Modifier.width(10.dp))
        PocketLibraryTheme(darkTheme = true) {
            BookTile(
                PreviewUtils.exampleBookBundle
            ) {}
        }
    }
}