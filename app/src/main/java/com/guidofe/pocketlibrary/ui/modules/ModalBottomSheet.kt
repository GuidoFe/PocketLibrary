package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider

@Composable
fun ModalBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val visibleState = remember {
        MutableTransitionState(false)
    }
    val transition = updateTransition(visibleState, label = "transition")
    val background by transition.animateColor(
        transitionSpec = { tween(300) },
        label = "background"
    ) {
        if (it) MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f) else Color.Transparent
    }
    val yOffset by transition.animateFloat(
        transitionSpec = { tween(300) },
        label = "offset"
    ) {
        if (it) 0f else 1f
    }
    LaunchedEffect(visible) {
        visibleState.targetState = visible
    }
    if (visibleState.currentState || !visibleState.isIdle) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset = IntOffset(0, 0)
            },
            onDismissRequest = { onDismiss() }
        ) {
            BoxWithConstraints {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(background)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { onDismiss() },
                                onTap = { onDismiss() }
                            )
                        }
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge.copy(
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        ),
                        shadowElevation = 1.dp,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            // .fillMaxWidth()
                            .width(
                                min(
                                    640.dp,
                                    with(LocalDensity.current) {
                                        configuration.screenWidthDp.dp
                                    }
                                )
                            )
                            .align(Alignment.BottomCenter)
                            .offset(0.dp, y = this@BoxWithConstraints.maxHeight * yOffset)
                    ) {
                        Column {
                            content()
                        }
                    }
                }
            }
        }
    }
}