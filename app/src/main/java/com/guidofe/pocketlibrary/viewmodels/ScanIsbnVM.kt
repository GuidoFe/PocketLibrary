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
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.utils.distance
import com.guidofe.pocketlibrary.viewmodels.interfaces.IScanIsbnVM
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

const val MIN_HEIGHT = 1080
const val MIN_WIDTH = 1920

@ExperimentalGetImage @HiltViewModel
class ScanIsbnVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
) : ViewModel(), IScanIsbnVM {
    override var cameraProvider: ProcessCameraProvider? = null
    private val scannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()
    private var imageAnalysis: ImageAnalysis? = null
    private val scanner = BarcodeScanning.getClient(scannerOptions)
    override var scannedCode: String? by mutableStateOf(null)
    override val translationDialogState = TranslationDialogState()
    // private set
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
                        // cameraProvider?.unbind(imageAnalysis)
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
    ) : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                val imageCenter = IntOffset(image.width, image.height)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        var bestBarcode: Barcode? = null
                        var bestDistance = Double.POSITIVE_INFINITY
                        for (barcode in barcodes) {
                            if (barcode.valueType == Barcode.TYPE_ISBN) {
                                barcode.boundingBox?.let { rect ->
                                    val distance = imageCenter.distance(
                                        IntOffset(rect.centerX(), rect.centerY())
                                    )
                                    if (distance < bestDistance) {
                                        bestBarcode = barcode
                                        bestDistance = distance
                                    }
                                }
                            } else {
                                // TODO: popup error
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