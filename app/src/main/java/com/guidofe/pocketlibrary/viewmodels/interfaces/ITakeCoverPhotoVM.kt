package com.guidofe.pocketlibrary.viewmodels.interfaces

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface ITakeCoverPhotoVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun getImageCapture(): ImageCapture
    var cameraProvider: ProcessCameraProvider?
    fun takePhoto(
        uri: Uri,
        onError: (ImageCaptureException) -> Unit,
        onImageSaved: (ImageCapture.OutputFileResults) -> Unit
    )
}