package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertIsbnDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var isbn by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(
                stringResource(R.string.type_the_isbn),
                modifier = Modifier.padding(bottom = 8.dp)

            )
        },
        text = {
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                placeholder = { Text("ISBN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(isbn)
            }) {
                Text(stringResource(R.string.ok_label))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = {
            onDismiss()
        }
    )
}

@Composable
@Preview
private fun InsertIsbnDialogPreview() {
    InsertIsbnDialog({}, {})
}