package com.guidofe.pocketlibrary.viewmodels.interfaces

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.Handle

interface ICoverEditorVM {
    val scaffoldState: ScaffoldState
    val snackbarHostState: SnackbarHostState
    fun initializeImage(uri: Uri)
    var image: ImageBitmap?
    var resizedImageHeight: Int
    var resizedImageWidth: Int
    var handleA: Handle
    var handleB: Handle
    var rectangleStart: Offset
    val pointerRadius: Dp
    var isPointAMoving: Boolean
    var isPointBMoving: Boolean
    var currentlyDraggedHandle: Handle?
    var rectangleSize: Size
    var isDraggingRect: Boolean
    fun isPointInsideRect(point: Offset): Boolean
    fun cropAndSave(path: String, callback: () -> Unit)
}