package com.guidofe.pocketlibrary.viewmodels.previews

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ITakeCoverPhotoVM

class TakeCoverPhotoVMPreview : ITakeCoverPhotoVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()

    override fun getImageCapture(): ImageCapture {
        return ImageCapture.Builder().build()
    }

    override var cameraProvider: ProcessCameraProvider? = null
    override fun takePhoto(
        uri: Uri,
        onError: (ImageCaptureException) -> Unit,
        onImageSaved: (ImageCapture.OutputFileResults) -> Unit
    ) {
    }
}