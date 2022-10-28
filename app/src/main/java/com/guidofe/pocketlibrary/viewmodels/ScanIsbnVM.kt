package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs

const val MIN_HEIGHT = 1080
const val MIN_WIDTH = 1920

@ExperimentalGetImage @HiltViewModel
class ScanIsbnVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
    ): ViewModel(), IScanIsbnVM {
    override var cameraProvider: ProcessCameraProvider? = null
    private val scannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()
    private var imageAnalysis: ImageAnalysis? = null
    private val scanner = BarcodeScanning.getClient(scannerOptions)
    override var scannedCode: String? by mutableStateOf(null)
        //private set
    override fun getImageAnalysis(): ImageAnalysis {
        if (imageAnalysis == null) {
            this.imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(MIN_WIDTH, MIN_HEIGHT))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis?.let {
                Log.d("debug", "setting analyzer")
                it.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    Analyzer(scanner) { isbn ->
                        scannedCode = isbn
                        Log.d("debug", "ISBN: $scannedCode")
                        //cameraProvider?.unbind(imageAnalysis)
                        imageAnalysis?.clearAnalyzer()
                    }
                )
            }
        }
        return imageAnalysis!!
    }

    override fun restartAnalysis(lifecycleOwner: LifecycleOwner) {
        Log.d("debug", "Restarting analyzer")

        imageAnalysis?.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            Analyzer(scanner) { isbn ->
                scannedCode = isbn
                Log.d("debug", "ISBN: $scannedCode")
                imageAnalysis?.clearAnalyzer()
            }
        )
    }

    private class Analyzer(
        val scanner: BarcodeScanner,
        val onSuccess: (String) -> Unit
    ): ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        var bestBarcode: Barcode? = null
                        var bestSize = -1
                        for (barcode in barcodes) {
                            if (barcode.valueType == Barcode.TYPE_ISBN) {
                                val rect = barcode.boundingBox
                                val newSize = if (rect != null)
                                    abs(rect.right - rect.left) * (rect.bottom - rect.top)
                                else
                                    0
                                if (bestBarcode == null || bestSize < newSize) {
                                    bestBarcode = barcode
                                    bestSize = newSize
                                }
                            } else {
                                //TODO: popup error
                                Log.w("debug", "Barcode ${barcode.displayValue} is not ISBN")
                            }
                        }
                        bestBarcode?.displayValue?.let {
                            onSuccess(it)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("debug", "error in scanning barcode", exception)

                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else
                imageProxy.close()
        }
    }
}