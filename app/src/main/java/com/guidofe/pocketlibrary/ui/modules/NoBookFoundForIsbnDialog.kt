package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R

@Composable
fun NoBookFoundForIsbnDialog(
    onDismiss: () -> Unit,
    onAddManually: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onAddManually,
            ) {
                Text(stringResource(R.string.insert_manually))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = {Text(stringResource(R.string.book_not_found))}
    )
}