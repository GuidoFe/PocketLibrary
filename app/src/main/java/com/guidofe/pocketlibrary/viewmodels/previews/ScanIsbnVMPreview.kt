package com.guidofe.pocketlibrary.viewmodels.previews

import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM

class ScanIsbnVMPreview : IScanIsbnVM {
    override var scannedCode: String? = null

    override fun getImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder().build()
    }

    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override var cameraProvider: ProcessCameraProvider? = null

    override fun restartAnalysis(lifecycleOwner: LifecycleOwner) {
    }
}