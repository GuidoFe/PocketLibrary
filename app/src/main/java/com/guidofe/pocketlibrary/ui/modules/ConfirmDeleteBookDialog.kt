package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R

@Composable
fun ConfirmDeleteBookDialog(isPlural: Boolean = false, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        text = {
            Text(stringResource(
                id = if(isPlural)
                    R.string.confirm_delete_books
                else
                    R.string.confirm_delete_book
            ))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismiss,
    )
}