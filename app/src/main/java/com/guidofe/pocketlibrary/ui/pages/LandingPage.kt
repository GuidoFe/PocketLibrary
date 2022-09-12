package com.guidofe.pocketlibrary.ui.pages.landing

import android.os.Bundle
import android.os.Environment
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.ui.pages.EditBookPage
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.utils.DestinationsNavigatorPlaceholder
import com.guidofe.pocketlibrary.viewmodels.FabAddBookViewModel
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalMaterialApi
@Destination(start = true)
@Composable
fun LandingPage(navigator: DestinationsNavigator) {
    val viewModel: ScanIsbnViewModel = hiltViewModel()
    var isbnText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            //.padding(paddingValues)
    ) {
        Column() {
            TextField(
                value = isbnText,
                onValueChange = {value ->
                    isbnText = value
                },
                label = {Text("ISBN")}
            )
            Button(
                onClick = {
                    scope.launch {
                        viewModel.getImportedBookFromIsbn(isbnText, callback = { importedBook: ImportedBookData? ->
                           if (importedBook != null) {
                               navigator.navigate(EditBookPageDestination(importedBookData = importedBook))
                           }
                        },
                        failureCallback = {

                        })
                    }
                },
                content = {Text("Send")}
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(DestinationsNavigatorPlaceholder())
    }
}