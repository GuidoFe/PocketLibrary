package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CornerIcon(
    cornerAlignment: Alignment,
    roundCornerSize: CornerSize,
    background: Color,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    var topStart = CornerSize(0.dp)
    var topEnd = CornerSize(0.dp)
    var bottomEnd = CornerSize(0.dp)
    var bottomStart = CornerSize(0.dp)
    when (cornerAlignment) {
        Alignment.TopStart -> topStart = roundCornerSize
        Alignment.TopEnd -> topEnd = roundCornerSize
        Alignment.BottomEnd -> bottomEnd = roundCornerSize
        Alignment.BottomStart -> bottomStart = roundCornerSize
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(25.dp)
            .clip(
                RoundedCornerShape(
                    topStart,
                    topEnd,
                    bottomEnd,
                    bottomStart
                )
            )
            .background(background)
            .padding(3.dp)
    ) {
        icon()
    }
}