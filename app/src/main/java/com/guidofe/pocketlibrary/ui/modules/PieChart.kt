package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import kotlin.math.sqrt

@Composable
fun PieChart(
    circleThickness: Dp,
    innerCircleThickness: Dp,
    circleColor: Color,
    innerCircleColors: List<Color>,
    total: Float,
    values: List<Float>,
    modifier: Modifier = Modifier,
    startAngle: Float = -90f,
    cap: StrokeCap = StrokeCap.Round,
    content: @Composable BoxScope.() -> Unit = {}
) {
    if (innerCircleColors.size != values.size)
        return
    val sweepAngles = mutableListOf<Pair<Float, Float>>()
    var lastAngle = startAngle
    for (value in values) {
        val sweep = if (total != 0f) value / total * 360 else 0f
        sweepAngles.add(Pair(lastAngle, sweep))
        lastAngle += sweep
    }
    with(LocalDensity.current) {
        val circleThicknessPx = circleThickness.toPx()
        val innerCircleThicknessPx = innerCircleThickness.toPx()
        val circleInnerPaddingPx = (circleThicknessPx - innerCircleThicknessPx) / 2
        val innerCircleTopLeftPx = innerCircleThicknessPx / 2 + circleInnerPaddingPx
        BoxWithConstraints(
            modifier = modifier
        ) {
            val maxSize = min(maxWidth, maxHeight)
            val circleRadiusPx = maxSize.toPx() / 2 - circleThicknessPx / 2
            val contentBoxSize = (
                (circleRadiusPx - circleThicknessPx / 2) * sqrt(2.0).toFloat()
                ).toDp()
            Canvas(
                modifier = Modifier.size(maxSize),
            ) {
                drawCircle(
                    circleColor,
                    radius = circleRadiusPx,
                    center = this.center,
                    style = Stroke(circleThicknessPx),
                )
                for ((i, pair) in sweepAngles.withIndex().reversed()) {
                    if (pair.second != 0f) {
                        drawArc(
                            color = innerCircleColors[i],
                            startAngle = pair.first,
                            sweepAngle = pair.second,
                            useCenter = false,
                            topLeft = Offset(innerCircleTopLeftPx, innerCircleTopLeftPx),
                            size = Size(circleRadiusPx * 2, circleRadiusPx * 2),
                            style = Stroke(
                                width = innerCircleThicknessPx,
                                cap = cap
                            )
                        )
                    }
                }
            }
            Box(
                modifier = Modifier.size(contentBoxSize).align(Alignment.Center),
                content = content
            )
        }
    }
}