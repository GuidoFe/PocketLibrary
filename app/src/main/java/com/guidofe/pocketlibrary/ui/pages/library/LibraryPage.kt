package com.guidofe.pocketlibrary.ui.pages.library

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.LibraryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun LibraryPage(navigator: DestinationsNavigator) {
    val viewModel: LibraryViewModel = hiltViewModel()
    var manualIsbn: String by remember{mutableStateOf("")}
    if(viewModel.showInsertIsbnDialog.value) {
        AlertDialog(
            onDismissRequest = {
                viewModel.showInsertIsbnDialog.value = false
            },
            buttons = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(onClick = {
                        viewModel.fetchBookForTypedIsbn(
                            isbn = manualIsbn,
                            callback = { importedBookData ->
                                if(importedBookData != null) {
                                    //navController.navigate("edit_book?has_imported_book=true", importedBookData)
                                } else {
                                    //navController.navigate("edit_book")
                                }
                            },
                            failureCallback = {
                                Log.d("Test", "Failed finding book with code $it")
                            }

                            )
                    }) {
                        Text(text = stringResource(R.string.search))
                    }
                }
            },
            title = {Text(stringResource(R.string.isbn))},
            text = {
                TextField(
                    value = manualIsbn,
                    onValueChange = {new -> manualIsbn = new},
                    singleLine = true,
                    placeholder = {Text("978-1498427814")},
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        )
    }
    Scaffold(
        floatingActionButton = {
            AddBookFab(
                onInsertIsbnManuallyClick = {
                    viewModel.showInsertIsbnDialog.value = true
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            //.padding(paddingValues)
    ) {
      Surface(
          modifier = Modifier
      ) {
          Text("Library Page")
      }
    }
}