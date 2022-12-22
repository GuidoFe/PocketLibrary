package com.guidofe.pocketlibrary.ui.pages

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.CoverEditorVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ICoverEditorVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import java.lang.Float.min
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Destination
fun CoverEditorPage(
    uri: Uri,
    resultBackNavigator: ResultBackNavigator<Uri>,
    vm: ICoverEditorVM = hiltViewModel<CoverEditorVM>()
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                vm.initializeImage(uri)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        // TODO: make title @Composable () -> Unit instead of string? No more need for context
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.cover_editor)) },
            navigationIcon = {
                IconButton(
                    onClick = {
                        resultBackNavigator.navigateBack()
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        contentDescription = stringResource(R.string.cancel)
                    )
                }
            }
        ) {
            IconButton(
                onClick = {
                    uri.path?.let { path ->
                        Log.d("debug", "Path is not null")
                        vm.cropAndSave(path) {
                            Log.d("debug", "Croped and saved")
                            coroutineScope.launch(Dispatchers.Main) {
                                resultBackNavigator.navigateBack(uri)
                            }
                        }
                    }
                }
            ) {
                Icon(
                    painterResource(R.drawable.check_24px),
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        if (vm.handleA.isPointInside(it))
                            vm.currentlyDraggedHandle = vm.handleA
                        else if (vm.handleB.isPointInside(it))
                            vm.currentlyDraggedHandle = vm.handleB
                        else if (vm.isPointInsideRect(it))
                            vm.isDraggingRect = true
                    },
                    onDragEnd = {
                        vm.currentlyDraggedHandle = null
                        vm.isDraggingRect = false
                    },
                    onDragCancel = {}
                ) { change, _ ->
                    if (vm.isDraggingRect) {
                        val movementVec = change.position.minus(change.previousPosition)
                        for (handle in listOf(vm.handleA, vm.handleB)) {
                            val newOffset = handle.offset.plus(movementVec)
                            handle.offset = Offset(
                                x = min(max(0f, newOffset.x), vm.resizedImageWidth.toFloat()),
                                y = min(max(0f, newOffset.y), vm.resizedImageHeight.toFloat())
                            )
                        }
                    } else {
                        val position = change.position
                        vm.currentlyDraggedHandle?.let { handle ->
                            handle.offset = Offset(
                                x = min(max(0f, position.x), vm.resizedImageWidth.toFloat()),
                                y = min(max(0f, position.y), vm.resizedImageHeight.toFloat())
                            )
                        }
                    }
                }
            }
        // .padding(20.dp)
    ) {
        LaunchedEffect(true) {
            with(density) {
                val pointerRadiusPx = vm.pointerRadius.toPx()
                vm.handleA.radius = pointerRadiusPx
                vm.handleB.radius = pointerRadiusPx
            }
        }
        LaunchedEffect(vm.resizedImageWidth, vm.resizedImageHeight) {
            if (vm.resizedImageWidth > 0 && vm.resizedImageHeight > 0) {
                with(density) {
                    val radiusPx = vm.pointerRadius.toPx()
                    vm.handleA.offset = Offset(
                        radiusPx,
                        radiusPx
                    )
                    vm.handleB.offset = Offset(
                        vm.resizedImageWidth.toFloat() - radiusPx,
                        vm.resizedImageHeight.toFloat() - radiusPx
                    )
                }
            }
        }
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            vm.image?.let { image ->
                val canvasHeight = maxHeight.roundToPx()
                val canvasWidth = maxWidth.roundToPx()
                val canvasRatio = canvasWidth.toFloat() / canvasHeight.toFloat()
                val imageRatio = image.width.toFloat() / image.height.toFloat()
                vm.resizedImageWidth = if (imageRatio > canvasRatio) {
                    canvasWidth
                } else {
                    (canvasHeight * imageRatio).roundToInt()
                }
                vm.resizedImageHeight = if (imageRatio > canvasRatio) {
                    (canvasWidth / imageRatio).roundToInt()
                } else {
                    canvasHeight
                }
                drawImage(
                    image,
                    dstSize = IntSize(
                        vm.resizedImageWidth,
                        vm.resizedImageHeight
                    )
                )
                vm.rectangleStart = Offset(
                    minOf(vm.handleA.offset.x, vm.handleB.offset.x),
                    minOf(vm.handleA.offset.y, vm.handleB.offset.y)
                )
                vm.rectangleSize = Size(
                    (vm.handleB.offset.x - vm.handleA.offset.x).absoluteValue,
                    (vm.handleB.offset.y - vm.handleA.offset.y).absoluteValue,
                )
                drawRect(
                    Color.White,
                    topLeft = vm.rectangleStart,
                    size = vm.rectangleSize,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(Color.White, radius = vm.handleA.radius, center = vm.handleA.offset)
                drawCircle(
                    Color.White, radius = vm.handleB.radius, center = vm.handleB.offset
                )
            }
        })
    }
}