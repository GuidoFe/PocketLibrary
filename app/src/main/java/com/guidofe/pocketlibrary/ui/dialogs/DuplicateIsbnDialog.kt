package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R

@Composable
fun DuplicateIsbnDialog(
    onAddAnyway: () -> Unit,
    onCancel: () -> Unit,
    title: String? = null,
    authors: String? = null,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            Button(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        },
        dismissButton = {
            TextButton(onClick = onAddAnyway) { Text(stringResource(R.string.add_anyway)) }
        },
        title = { Text(stringResource(R.string.isbn_already_present)) },
        text = {
            if (title != null && authors != null)
                Text(
                    stringResource(
                        R.string.isbn_already_present_specific_book_dialog_text
                    ).format(title, authors)
                )
            else if (title != null)
                Text(
                    stringResource(
                        R.string.isbn_already_present_specific_title_dialog_text
                    ).format(title)
                )
            else
                Text(stringResource(R.string.isbn_already_present_generic_dialog_text))
        }
    )
}