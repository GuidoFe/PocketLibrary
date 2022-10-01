package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun HorizontalDivider(width: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .width(width))
}

@Composable
fun VerticalDivider(height: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .height(height))
}
