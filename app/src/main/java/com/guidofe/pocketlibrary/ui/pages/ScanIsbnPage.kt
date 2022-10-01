package com.guidofe.pocketlibrary.ui.pages

import android.Manifest
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.IScanIsbnViewModel
import com.guidofe.pocketlibrary.viewmodels.ScanIsbnViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.MutableStateFlow

private fun submitIsbnEffect(viewModel: IScanIsbnViewModel, navigator: DestinationsNavigator) {
    Log.d("debug", "Code != null")
    viewModel.getImportedBookFromIsbn(
        viewModel.code!!,
        callback = { importedBook: ImportedBookData? ->
            if (importedBook != null) {
                navigator.navigate(EditBookPageDestination(importedBookData = importedBook))
            } else {
                Log.w("debug", "Book not found")
                viewModel.displayBookNotFoundDialog = true
            }
        },
        failureCallback = { code: Int, message: String ->
            viewModel.errorMessage = "$code: $message"
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Destination(route = "isbn_scan")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun ScanIsbnPage(
    navigator: DestinationsNavigator,
    viewModel: IScanIsbnViewModel = hiltViewModel<ScanIsbnViewModel>()
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.appBarDelegate.setAppBarContent(
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
            CameraView(additionalUseCases = arrayOf(viewModel.getImageAnalysis()))
            if (viewModel.code != null) {
                LaunchedEffect(key1 = viewModel.code) {
                    submitIsbnEffect(viewModel = viewModel, navigator = navigator)
                }
            }
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
    if (viewModel.displayBookNotFoundDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.book_not_found)) },
            text = { Text(stringResource(R.string.book_not_found_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.displayBookNotFoundDialog = false
                    navigator.navigate(EditBookPageDestination())
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
    if (viewModel.displayInsertIsbnDialog) {
        InsertIsbnDialog(onConfirm = { isbn ->
            submitIsbnEffect(viewModel = viewModel, navigator = navigator)
            viewModel.displayInsertIsbnDialog = false
        }, onDismiss = {
            viewModel.displayInsertIsbnDialog = false
        })
    }
    if (viewModel.errorMessage != null) {
        AlertDialog(
            title = { Text(stringResource(R.string.error)) },
            text = { Text(viewModel.errorMessage?:"") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.errorMessage = null
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            onDismissRequest = {
                viewModel.errorMessage = null
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
        viewModel = object: IScanIsbnViewModel {
            override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(MutableStateFlow(null))
            override var displayBookNotFoundDialog = false
            override var displayInsertIsbnDialog: Boolean = false
            override var errorMessage: String? = null

            override fun getImportedBookFromIsbn(
                isbn: String,
                callback: (book: ImportedBookData?) -> Unit,
                failureCallback: (code: Int, message: String) -> Unit
            ) {
            }

            override var code: String? = ""
                private set

            override fun getImageAnalysis(): ImageAnalysis {
                return ImageAnalysis.Builder().build()
            }

        }
    )
}