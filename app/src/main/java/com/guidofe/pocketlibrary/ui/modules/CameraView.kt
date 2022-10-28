package com.guidofe.pocketlibrary.ui.modules

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    additionalUseCases: Array<UseCase> = arrayOf(),
    onCameraProviderSet: (ProcessCameraProvider) -> Unit = {}
) {
    // 1
    val lifecycleOwner = LocalLifecycleOwner.current

    
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
}