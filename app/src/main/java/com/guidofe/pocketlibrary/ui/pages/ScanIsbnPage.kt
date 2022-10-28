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
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.destinations.BookDisambiguationPageDestination
import com.guidofe.pocketlibrary.ui.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.modules.*
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
    scanVm: IScanIsbnVM = hiltViewModel<ScanIsbnVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    resultRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>
) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var startSavingFlow by remember{mutableStateOf(false)}
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

    LaunchedEffect(scanVm.code) {
        scanVm.code?.let { isbn ->
            importVm.getLibraryBooksWithSameIsbn(isbn) { ownedList ->
                if (ownedList.isEmpty()) {
                    startSavingFlow = true
                } else {
                    Snackbars.bookAlreadyPresentSnackbar(
                        scanVm.snackbarHostState,
                        context,
                        coroutine,
                        onDismiss = {scanVm.restartAnalysis(lifecycleOwner)}
                    ) {
                        startSavingFlow = true
                    }
                }
            }
        }
    }

    LaunchedEffect(startSavingFlow) {
        if (!startSavingFlow) return@LaunchedEffect
        scanVm.code?.let { isbn ->
            importVm.getAndSaveBookFromIsbnFlow(
                isbn = isbn,
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
            startSavingFlow = false
        }
    }

    resultRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBookInDb(navResult.value) {
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
        object: IScanIsbnVM {
            override var code: String? = ""
            override fun getImageAnalysis(): ImageAnalysis {
                return ImageAnalysis.Builder().build()
            }
            override val scaffoldState = ScaffoldState()
            override val snackbarHostState = SnackbarHostState()
            override fun restartAnalysis(lifecycleOwner: LifecycleOwner) {}
            override var cameraProvider: ProcessCameraProvider? = null

        },
        object: IImportedBookVM {
            override fun getImportedBooksFromIsbn(
                isbn: String,
                failureCallback: (message: String) -> Unit,
                maxResults: Int,
                callback: (books: List<ImportedBookData>) -> Unit
            ) {}

            override fun getLibraryBooksWithSameIsbn(
                isbn: String,
                callback: (List<BookBundle>) -> Unit
            ) {}

            override fun saveImportedBookInDb(
                importedBook: ImportedBookData,
                callback: (Long) -> Unit
            ) {}

            override fun getAndSaveBookFromIsbnFlow(
                isbn: String,
                onNetworkError: () -> Unit,
                onNoBookFound: () -> Unit,
                onOneBookSaved: () -> Unit,
                onMultipleBooksFound: (List<ImportedBookData>) -> Unit
            ) {}
        },
        EmptyResultRecipient()
    )
}