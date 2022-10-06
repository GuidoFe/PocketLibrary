package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.IScanIsbnViewModel
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Destination(start = true)
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    viewModel: IScanIsbnViewModel = hiltViewModel<ScanIsbnViewModel>(),
) {
    var isbnText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.appBarDelegate.setAppBarContent(
            AppBarState(
                title = context.getString(R.string.my_library)
            )
        )
    }
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
                           //navigator.navigate(EditBookPageDestination(importedBookData = importedBook))
                       }
                    },
                    failureCallback = { code: Int, msg: String ->

                    })
                }
            },
            content = {Text("Send")}
        )
        Button(
            onClick = {
                navigator.navigate(ScanIsbnPageDestination())
            },
            content = {Text("Scan")}
        )
        val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }

    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
fun  LandingPagePreview() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(
            EmptyDestinationsNavigator,
            object: IScanIsbnViewModel {
                override var displayBookNotFoundDialog: Boolean = false
                override var displayInsertIsbnDialog: Boolean = false
                override var errorMessage: String? = null

                override fun getImportedBookFromIsbn(
                    isbn: String,
                    callback: (book: ImportedBookData?) -> Unit,
                    failureCallback: (code: Int, message: String) -> Unit
                ) {
                }

                override var code: String? = ""

                override fun getImageAnalysis(): ImageAnalysis {
                    return ImageAnalysis.Builder().build()
                }

                override val appBarDelegate: AppBarStateDelegate =
                    PreviewUtils.fakeAppBarStateDelegate

            }
        )
    }
}