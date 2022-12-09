package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R

@Composable
fun ConfirmExitDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        text = { Text(stringResource(R.string.data_not_saved_dialog)) },
        confirmButton = {
            Button(
                onClick = { onCancel() }

            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        dismissButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(R.string.imsure))
            }
        }
    )
}