package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.math.abs

const val MIN_HEIGHT = 1080
const val MIN_WIDTH = 1920

@ExperimentalGetImage @HiltViewModel
class ScanIsbnViewModel @Inject constructor(
    private val repo: BookMetaRepository,
    private val appBarState: MutableStateFlow<AppBarState?>,
    ): ViewModel(), IScanIsbnViewModel {
    private val scannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13)
        .build()

    private var imageAnalysis: ImageAnalysis? = null
    override var errorMessage: String? by mutableStateOf(null)
    override var displayBookNotFoundDialog: Boolean by mutableStateOf(false)
    override var displayInsertIsbnDialog: Boolean by mutableStateOf(false)
    override fun getImportedBookFromIsbn(isbn: String, callback: (book: ImportedBookData?) -> Unit, failureCallback: (code: Int, message: String) -> Unit) {
        repo.fetchVolumeByIsbn(isbn, callback, failureCallback)
    }
    override var code: String? by mutableStateOf(null)
        private set
    override fun getImageAnalysis(): ImageAnalysis {
        if (imageAnalysis == null) {
            this.imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(MIN_WIDTH, MIN_HEIGHT))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
        } else
            this.imageAnalysis?.clearAnalyzer()
        val scanner = BarcodeScanning.getClient(scannerOptions)
        this.imageAnalysis?.setAnalyzer(Executors.newSingleThreadExecutor(), ImageAnalysis.Analyzer { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                val result = scanner.process(image)
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
                        if (bestBarcode != null) {
                            code = bestBarcode.displayValue
                            Log.d("debug", "ISBN: $code")
                            imageAnalysis?.clearAnalyzer()
                        }
                    }
                    .addOnFailureListener {  exception ->
                        Log.e("debug", "error in scanning barcode", exception)

                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else
                imageProxy.close()
        })
        return imageAnalysis!!
    }

    override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(appBarState)

}