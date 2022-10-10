package com.guidofe.pocketlibrary.ui.pages

import android.Manifest
import androidx.camera.core.ImageAnalysis
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.modules.CameraView
import com.guidofe.pocketlibrary.ui.modules.InsertIsbnDialog
import com.guidofe.pocketlibrary.ui.pages.destinations.ChooseImportedBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

private fun submitIsbnEffect(scanVm: IScanIsbnVM, importedBookVm: IImportedBookVM, navigator: DestinationsNavigator) {
    importedBookVm.getImportedBooksFromIsbn(
        scanVm.code!!,
        callback = { importedBooks: List<ImportedBookData> ->
            when (importedBooks.size) {
                0 -> scanVm.displayBookNotFoundDialog = true
                1 -> {
                    importedBookVm.saveImportedBookInDb(importedBooks[0]) { id ->
                        navigator.navigate(ViewBookPageDestination(id))
                    }
                }
                else -> {
                    navigator.navigate(ChooseImportedBookPageDestination(importedBooks.toTypedArray()))
                }
            }
        },
        failureCallback = { _: Int,_: String ->
            scanVm.displayConnectionErrorDialog = true
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = "isbn_scan")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun ScanIsbnPage(
    navigator: DestinationsNavigator,
    scanVm: IScanIsbnVM = hiltViewModel<ScanIsbnVM>(),
    importedBookVm: IImportedBookVM = hiltViewModel<ImportedBookVM>()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        scanVm.appBarDelegate.setAppBarContent(
            AppBarState(title=context.getString(R.string.scan_isbn)
            )
        )
    }
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            if (scanVm.code != null) {
                LaunchedEffect(key1 = scanVm.code) {
                    submitIsbnEffect(scanVm, importedBookVm, navigator)
                }
            }
            CameraView(additionalUseCases = arrayOf(scanVm.getImageAnalysis()))
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
    if (scanVm.displayBookNotFoundDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.book_not_found)) },
            text = { Text(stringResource(R.string.book_not_found_message)) },
            confirmButton = {
                TextButton(onClick = {
                    scanVm.displayBookNotFoundDialog = false
                    //navigator.navigate(EditBookPageDestination())
                }) {
                    Text(stringResource(R.string.ok))
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
    }
    if (scanVm.displayInsertIsbnDialog) {
        InsertIsbnDialog(onConfirm = { isbn ->
            scanVm.displayInsertIsbnDialog = false
            scanVm.code = isbn
        }, onDismiss = {
            scanVm.displayInsertIsbnDialog = false
        })
    }
    if (scanVm.displayConnectionErrorDialog || scanVm.displayBookNotFoundDialog) {
        AlertDialog(
            title = {
                Text(stringResource(
                    if(scanVm.displayConnectionErrorDialog)
                        R.string.error_no_connection
                    else
                        R.string.book_not_found
                ))},
            text = {
                Text(stringResource(
                    if(scanVm.displayConnectionErrorDialog)
                        R.string.no_connection_text
                    else
                        R.string.book_not_found_message
                ))},
            confirmButton = {
                Button(
                    content = {Text(stringResource(R.string.insert_manually))},
                    onClick = {
                        scanVm.displayConnectionErrorDialog = false
                        navigator.navigate(EditBookPageDestination(isbn = scanVm.code))
                    }
                )
            },
            dismissButton = {
                TextButton(
                    content = {Text(stringResource(R.string.cancel))},
                    onClick = {
                        scanVm.displayConnectionErrorDialog = false
                        navigator.popBackStack()
                    }
                )
            },
            onDismissRequest = {
                scanVm.displayConnectionErrorDialog = false
                navigator.navigateUp()
            }
        )
    }
    if (scanVm.errorMessage != null) {
        AlertDialog(
            title = { Text(stringResource(R.string.error)) },
            text = { Text(scanVm.errorMessage?:"") },
            confirmButton = {
                TextButton(onClick = {
                    scanVm.errorMessage = null
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            onDismissRequest = {
                scanVm.errorMessage = null
                navigator.navigateUp()
            }
        )
    }
}

//TODO: detect double isbn
@Composable
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
fun ScanIsbnPagePreview() {
    ScanIsbnPage(
        navigator = EmptyDestinationsNavigator,
        scanVm = object: IScanIsbnVM {
            override var coverUrl: String = ""
            override var displayBookNotFoundDialog = false
            override var displayInsertIsbnDialog: Boolean = false
            override var displayConnectionErrorDialog: Boolean = false
            override var errorMessage: String? = null
            override var code: String? = ""
            override fun getImageAnalysis(): ImageAnalysis {
                return ImageAnalysis.Builder().build()
            }

            override val appBarDelegate: AppBarStateDelegate =
                PreviewUtils.fakeAppBarStateDelegate

        },
        importedBookVm = object: IImportedBookVM {
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