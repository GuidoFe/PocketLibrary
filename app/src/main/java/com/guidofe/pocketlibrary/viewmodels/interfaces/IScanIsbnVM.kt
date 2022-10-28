package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState

interface IScanIsbnVM {
    var scannedCode: String?
    fun getImageAnalysis(): ImageAnalysis
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    var cameraProvider: ProcessCameraProvider?
    fun restartAnalysis(lifecycleOwner: LifecycleOwner)
}