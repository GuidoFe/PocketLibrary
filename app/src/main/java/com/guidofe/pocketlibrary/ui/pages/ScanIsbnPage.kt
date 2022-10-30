package com.guidofe.pocketlibrary.ui.pages

import android.Manifest
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.BookDisambiguationPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Destination(route = "isbn_scan")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun ScanIsbnPage(
    navigator: DestinationsNavigator,
    destination: BookDestination = BookDestination.LIBRARY,
    scanVm: IScanIsbnVM = hiltViewModel<ScanIsbnVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    resultRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var isbnToSearch: String? by remember{mutableStateOf(null)}
    var showBookNotFoundDialog by remember{mutableStateOf(false)}
    LaunchedEffect(key1 = true) {
        scanVm.scaffoldState.refreshBar(title=context.getString(R.string.scan_isbn))
    }
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            CameraView(
                additionalUseCases = arrayOf(scanVm.getImageAnalysis()),
                onCameraProviderSet = {scanVm.cameraProvider = it}
            )
        }
        is PermissionStatus.Denied -> {
            if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                AlertDialog(
                    title = {Text(stringResource(R.string.permission_required))},
                    text = {Text(stringResource(R.string.isbn_camera_rationale))},
                    confirmButton = {
                        TextButton(onClick = {
                            cameraPermissionState.launchPermissionRequest()
                        }) {
                            Text(stringResource(R.string.request_permission))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            navigator.navigateUp()
                        }) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    },
                    onDismissRequest = {
                        navigator.navigateUp()
                    }
                )
            } else {
                LaunchedEffect(key1 = cameraPermissionState.status) {
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        }
    }

    LaunchedEffect(scanVm.scannedCode) {
        scanVm.scannedCode?.let { isbn ->
            val callback = { ownedList: List<Book> ->
                if (ownedList.isEmpty()) {
                    isbnToSearch = isbn
                } else {
                    Snackbars.bookAlreadyPresentSnackbar(
                        scanVm.snackbarHostState,
                        context,
                        coroutine,
                        onDismiss = {scanVm.restartAnalysis(lifecycleOwner)}
                    ) {
                        isbnToSearch = isbn
                    }
                }
            }
            when (destination) {
                BookDestination.LIBRARY -> importVm.getBooksInLibraryWithSameIsbn(isbn, callback)
                BookDestination.WISHLIST -> importVm.getBooksInWishlistWithSameIsbn(isbn, callback)
            }
        }
    }

    LaunchedEffect(isbnToSearch) {
        isbnToSearch?.let { isbn ->
            importVm.getAndSaveBookFromIsbnFlow(
                isbn = isbn,
                destination = destination,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(
                        scanVm.snackbarHostState,
                        context,
                        coroutine
                    )
                },
                onNoBookFound = {
                    showBookNotFoundDialog = true
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(
                        scanVm.snackbarHostState,
                        context,
                        coroutine,
                        onDismiss = {
                            scanVm.restartAnalysis(lifecycleOwner)
                        }
                    )
                },
                onMultipleBooksFound = { list ->
                    navigator.navigate(
                        BookDisambiguationPageDestination(list.toTypedArray())
                    )
                }
            )
        }
    }

    resultRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(navResult.value, destination) {
                Snackbars.bookSavedSnackbar(scanVm.snackbarHostState, context, coroutine) {}
            }
        }
    }
    if (showBookNotFoundDialog) {
        NoBookFoundForIsbnDialog(
            onDismiss = {
                showBookNotFoundDialog = false
                scanVm.restartAnalysis(lifecycleOwner)
            },
            onAddManually = {
                navigator.navigate(EditBookPageDestination())
            }
        )
    }

}

@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
private fun ScanIsbnPagePreview() {
    ScanIsbnPage(
        EmptyDestinationsNavigator,
        BookDestination.LIBRARY,
        object: IScanIsbnVM {
            override var scannedCode: String? = ""
            override fun getImageAnalysis(): ImageAnalysis {
                return ImageAnalysis.Builder().build()
            }
            override val scaffoldState = ScaffoldState()
            override val snackbarHostState = SnackbarHostState()
            override fun restartAnalysis(lifecycleOwner: LifecycleOwner) {}
            override var cameraProvider: ProcessCameraProvider? = null

        },
        object: IImportedBookVM {
            override val snackbarHostState = SnackbarHostState()

            override fun getImportedBooksFromIsbn(
                isbn: String,
                failureCallback: (message: String) -> Unit,
                maxResults: Int,
                callback: (books: List<ImportedBookData>) -> Unit
            ) {}

            override fun saveImportedBookAsBookBundle(
                importedBook: ImportedBookData,
                callback: (Long) -> Unit
            ) {}

            override fun getAndSaveBookFromIsbnFlow(
                isbn: String,
                destination: BookDestination,
                onNetworkError: () -> Unit,
                onNoBookFound: () -> Unit,
                onOneBookSaved: () -> Unit,
                onMultipleBooksFound: (List<ImportedBookData>) -> Unit
            ) {}

            override fun checkIfImportedBooksAreAlreadyInLibrary(
                list: List<ImportedBookData>,
                onAllOk: () -> Unit,
                onConflict: (booksOk: List<ImportedBookData>, duplicateBooks: List<ImportedBookData>) -> Unit
            ) {}

            override fun saveImportedBooksAsBookBundles(
                importedBooks: List<ImportedBookData>,
                callback: () -> Unit
            ) {}

            override fun getBooksInLibraryWithSameIsbn(
                isbn: String,
                callback: (List<Book>) -> Unit
            ) {}

            override fun saveImportedBook(
                importedBook: ImportedBookData,
                destination: BookDestination,
                callback: (Long) -> Unit
            ) {            }

            override fun saveImportedBooks(
                importedBooks: List<ImportedBookData>,
                destination: BookDestination,
                callback: () -> Unit
            ) {}

            override fun getBooksInWishlistWithSameIsbn(
                isbn: String,
                callback: (List<Book>) -> Unit
            ) {}
        },
        EmptyResultRecipient()
    )
}