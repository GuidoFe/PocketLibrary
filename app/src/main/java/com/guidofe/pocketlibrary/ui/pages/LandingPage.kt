package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Destination(start = true)
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    scanVm: IScanIsbnVM = hiltViewModel<ScanIsbnVM>(),
    importedBookVm: IImportedBookVM = hiltViewModel<ImportedBookVM>()
) {
    var isbnText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        scanVm.appBarDelegate.setAppBarContent(
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
                    importedBookVm.getImportedBooksFromIsbn(isbnText, callback = { importedBooks: List<ImportedBookData> ->
                       if (importedBooks.isNotEmpty()) {
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
            object: IScanIsbnVM {
                override var displayBookNotFoundDialog: Boolean = false
                override var displayInsertIsbnDialog: Boolean = false
                override var displayConnectionErrorDialog: Boolean = false
                override var errorMessage: String? = null

                override var code: String? = ""

                override fun getImageAnalysis(): ImageAnalysis {
                    return ImageAnalysis.Builder().build()
                }

                override var coverUrl: String = ""

                override val appBarDelegate: AppBarStateDelegate =
                    PreviewUtils.fakeAppBarStateDelegate

            },
            object: IImportedBookVM {
                override fun getImportedBooksFromIsbn(
                    isbn: String,
                    callback: (books: List<ImportedBookData>) -> Unit,
                    failureCallback: (code: Int, message: String) -> Unit
                ) {}

                override fun saveImportedBookInDb(
                    importedBook: ImportedBookData,
                    callback: (Long) -> Unit
                ) {}

            }
        )
    }
}