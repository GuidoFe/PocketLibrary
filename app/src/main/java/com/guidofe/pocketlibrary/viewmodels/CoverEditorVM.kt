package com.guidofe.pocketlibrary.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ICoverEditorVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class Handle() {
    var isMoving: Boolean = false
    var offset: Offset by mutableStateOf(Offset.Zero)
    var radius: Float by mutableStateOf(0f)

    fun isPointInside(pointOffset: Offset): Boolean {
        return offset.minus(pointOffset).getDistance() <= radius
    }
}

@HiltViewModel
class CoverEditorVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), ICoverEditorVM {
    override var image: ImageBitmap? by mutableStateOf(null)
    override var resizedImageWidth by mutableStateOf(0)
    override var resizedImageHeight by mutableStateOf(0)
    override var handleA: Handle = Handle()
    override var handleB: Handle = Handle()
    override var isPointAMoving = false
    override var isPointBMoving = false
    override val pointerRadius: Dp = 20.dp
    override var currentlyDraggedHandle: Handle? = null
    override var isDraggingRect: Boolean = false
    override var rectangleStart: Offset by mutableStateOf(Offset.Zero)
    override var rectangleSize: Size by mutableStateOf(Size.Zero)

    override fun isPointInsideRect(point: Offset): Boolean {
        return rectangleStart.x < point.x && point.x < (rectangleStart.x + rectangleSize.width) &&
            rectangleStart.y < point.y && point.y < (rectangleStart.y + rectangleSize.height)
    }

    override fun initializeImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val path = uri.path ?: return@launch
                val bitmap = BitmapFactory.decodeFile(path)
                val exif = ExifInterface(path)
                val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
                image = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                ).asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun cropAndSave(path: String, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                image?.asAndroidBitmap()?.let { bitmap ->
                    val ratio = bitmap.height.toFloat() / resizedImageHeight.toFloat()
                    val newImage = Bitmap.createBitmap(
                        bitmap,
                        (rectangleStart.x * ratio).roundToInt(),
                        (rectangleStart.y * ratio).roundToInt(),
                        (rectangleSize.width * ratio).roundToInt(),
                        (rectangleSize.height * ratio).roundToInt(),
                        null,
                        true
                    )
                    dataStore.saveCover(newImage, path) {
                        callback()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}