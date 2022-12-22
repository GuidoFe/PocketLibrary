package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BoxWithCorner(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    thickness: Dp = 3.dp,
    legLength: Dp = 20.dp
) {
    val density = LocalDensity.current
    val thicknessPx = with(density) { thickness.toPx() }
    val legLengthPx = with(density) { legLength.toPx() }
    Canvas(modifier = modifier) {
        val height = this.size.height
        val width = this.size.width
        // Top Left corner
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(legLengthPx, 0f),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, legLengthPx),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )

        // Top Right corner
        drawLine(
            color = color,
            start = Offset(width - legLengthPx, 0f),
            end = Offset(width, 0f),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(width, 0f),
            end = Offset(width, legLengthPx),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )

        // Bottom Left corner
        drawLine(
            color = color,
            start = Offset(0f, height - legLengthPx),
            end = Offset(0f, height),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(0f, height),
            end = Offset(legLengthPx, height),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )

        // Bottom Right corner
        drawLine(
            color = color,
            start = Offset(width - legLengthPx, height),
            end = Offset(width, height),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(width, height - legLengthPx),
            end = Offset(width, height),
            strokeWidth = thicknessPx,
            cap = StrokeCap.Round
        )
    }
}