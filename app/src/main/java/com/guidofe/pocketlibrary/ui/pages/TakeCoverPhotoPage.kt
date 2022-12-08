package com.guidofe.pocketlibrary.ui.pages

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.CoverEditorPageDestination
import com.guidofe.pocketlibrary.viewmodels.TakeCoverPhotoVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ITakeCoverPhotoVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Destination(route = "take_cover_photo")
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun TakeCoverPhotoPage(
    fileUri: Uri,
    resultBackNavigator: ResultBackNavigator<Uri>,
    navigator: DestinationsNavigator,
    vm: ITakeCoverPhotoVM = hiltViewModel<TakeCoverPhotoVM>(),
    imageEditorRecipient: ResultRecipient<CoverEditorPageDestination, Uri>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lineAlpha = 0.5f
    val lineSize = 3.dp
    val lineColor = Color.Black.copy(lineAlpha)
    imageEditorRecipient.onNavResult { result ->
        Log.d("debug", "Camera received result from editor")
        when (result) {
            is NavResult.Value -> {
                Log.d("debug", "Camera result is valid")
                resultBackNavigator.navigateBack(result.value)
            }
            is NavResult.Canceled -> resultBackNavigator.navigateBack()
        }
    }
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.take_photo_of_cover)) },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
    }
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    when (cameraPermissionState.status) {
        // If the camera permission is granted, then show screen with the feature enabled
        PermissionStatus.Granted -> {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                CameraView(
                    additionalUseCases = arrayOf(vm.getImageCapture()),
                    onCameraProviderSet = {
                        vm.cameraProvider = it
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(lineSize)
                        .offset(0.dp, maxHeight / 3)
                        .background(lineColor)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(lineSize)
                        .offset(0.dp, maxHeight / 3 * 2)
                        .background(lineColor)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(lineSize)
                        .offset(maxWidth / 3, 0.dp)
                        .background(lineColor)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(lineSize)
                        .offset(maxWidth / 3 * 2, 0.dp)
                        .background(lineColor)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            vm.takePhoto(
                                fileUri,
                                onError = {
                                    vm.cameraProvider?.unbindAll()
                                    coroutineScope.launch {
                                        vm.snackbarHostState.showSnackbar(
                                            CustomSnackbarVisuals(
                                                message = context.getString(
                                                    R.string.error_couldnt_save_image
                                                ),
                                                isError = true
                                            )
                                        )
                                    }
                                }
                            ) { result ->
                                vm.cameraProvider?.unbindAll()
                                result.savedUri?.let {
                                    navigator.navigate(
                                        CoverEditorPageDestination(it)
                                    )
                                }
                            }
                        }
                )
            }
        }
        is PermissionStatus.Denied -> {
            if ((cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                AlertDialog(
                    title = { Text(stringResource(R.string.permission_required)) },
                    text = { Text(stringResource(R.string.isbn_camera_rationale)) },
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
}