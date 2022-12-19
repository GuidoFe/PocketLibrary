package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreviewBookDialog(
    bookData: ImportedBookData?,
    onSaveButtonClicked: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = true),
        confirmButton = {
            Button(
                onClick = onSaveButtonClicked
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                bookData?.let { data ->
                    if (data.coverUrl != null) {
                        // TODO: placeholder for book cover
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(data.coverUrl)
                                .build(),
                            contentDescription = stringResource(id = R.string.cover),
                            modifier = Modifier.height(200.dp)
                        )
                    }
                    // TODO: change large cover placeholder
                    Text(
                        data.title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    data.subtitle?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(data.authors.joinToString(", "), textAlign = TextAlign.Center)
                    if (data.publisher != null && data.published != null)
                        Text(
                            "${data.publisher}, ${data.published}",
                            textAlign = TextAlign.Center
                        )
                    else {
                        data.publisher?.let { Text(it) }
                        data.published?.let { Text(it.toString()) }
                    }
                }
            }
        }
    )
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun PreviewPreviewBookDialog() {
    MaterialTheme {
        Box(
            modifier = Modifier.background(Color.Gray)
        ) {
            PreviewBookDialog(
                bookData = PreviewUtils.exampleImportedBook,
                onSaveButtonClicked = {},
                onDismissRequest = {},
            )
        }
    }
}