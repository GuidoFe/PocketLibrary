package com.guidofe.pocketlibrary.ui.modules

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.utils.isPermanentlyDenied

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    additionalUseCases: Array<UseCase> = arrayOf(),
    onCameraPermissionDenied: () -> Unit,
    onCameraProviderSet: (ProcessCameraProvider) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                when {
                    permissionState.status.isGranted -> {}
                    permissionState.status.shouldShowRationale ->
                        showRationaleDialog = true
                    permissionState.status.isPermanentlyDenied ->
                        showPermissionDeniedDialog = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Preview is incorrectly scaled in Compose on some devices without this
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                onCameraProviderSet(cameraProvider)
                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                try {
                    // Must unbind the use-cases before rebinding them.
                    cameraProvider.unbindAll()
                    val useCaseGroupBuilder = UseCaseGroup.Builder().addUseCase(preview)
                    for (useCase in additionalUseCases)
                        useCaseGroupBuilder.addUseCase(useCase)
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, useCaseGroupBuilder.build()
                    )
                } catch (e: Exception) {
                    Log.e("debug", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        }
    )

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            confirmButton = {
                Button(onClick = { showRationaleDialog = false }) {
                    Text(stringResource(R.string.deny))
                }
            },
            title = { Text(stringResource(R.string.permission_required)) },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        permissionState.launchPermissionRequest()
                        showRationaleDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ask_again))
                }
            },
            text = { Text(stringResource(R.string.isbn_camera_rationale)) }
        )
    }

    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDeniedDialog = false },
            confirmButton = {
                Button(onClick = { onCameraPermissionDenied() }) {
                    Text(stringResource(R.string.ok_label))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        permissionState.launchPermissionRequest()
                        showPermissionDeniedDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ask_again))
                }
            },
            text = { Text(stringResource(R.string.camera_denied)) }
        )
    }
}