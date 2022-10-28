package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalMaterial3Api::class)
@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun InsertIsbnDialog(
    resultNavigator: ResultBackNavigator<String>
) {
    var isbn by remember { mutableStateOf("")}
    AlertDialog(
        title = { Text(
            stringResource(R.string.type_the_isbn),
            modifier = Modifier.padding(bottom = 10.dp)

        ) },
        text = {
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                placeholder = { Text("ISBN") },
            )
        },
        confirmButton = {
            Button(onClick = {
                resultNavigator.navigateBack(isbn)
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                resultNavigator.navigateBack()
            }) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        onDismissRequest = {
            resultNavigator.navigateBack()
        }
    )
}

@Composable
@Preview
private fun InsertIsbnDialogPreview() {
    InsertIsbnDialog(EmptyResultBackNavigator())
}