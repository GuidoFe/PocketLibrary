package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ITakeCoverPhotoVM
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class TakeCoverPhotoVM @Inject constructor(
    override val snackbarHostState: SnackbarHostState,
    override val scaffoldState: ScaffoldState
) : ViewModel(), ITakeCoverPhotoVM {
    private var imageCapture: ImageCapture? = null
    override var cameraProvider: ProcessCameraProvider? = null
    override fun getImageCapture(): ImageCapture {
        return if (imageCapture == null) {
            imageCapture = ImageCapture.Builder()
                // .setTargetRotation()
                .build()
            imageCapture!!
        } else imageCapture!!
    }

    override fun takePhoto(
        uri: Uri,
        onError: (ImageCaptureException) -> Unit,
        onImageSaved: (ImageCapture.OutputFileResults) -> Unit
    ) {
        Log.d("debug", "File uri: ${uri.path}")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(uri.path!!)).build()
        imageCapture?.takePicture(
            outputFileOptions,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("debug", "Saved Uri path: ${outputFileResults.savedUri?.path}")
                    viewModelScope.launch(Dispatchers.Main) {
                        onImageSaved(outputFileResults)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    viewModelScope.launch(Dispatchers.Main) {
                        onError(exception)
                    }
                }
            }
        )
    }
}