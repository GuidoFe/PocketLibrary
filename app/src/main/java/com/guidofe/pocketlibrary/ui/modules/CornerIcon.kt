package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils

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
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.contentColorFor(background)
        ) {
            icon()
        }
    }
}

@Composable
@Preview
fun CornerIconPreview() {
    PreviewUtils.ThemeColumn() {
        val corner = MaterialTheme.shapes.medium.bottomEnd
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        ) {
            CornerIcon(
                cornerAlignment = Alignment.TopStart,
                roundCornerSize = corner,
                background = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(painterResource(R.drawable.delete_24px), "")
            }
            CornerIcon(
                cornerAlignment = Alignment.TopEnd,
                roundCornerSize = corner,
                background = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(painterResource(R.drawable.delete_24px), "")
            }
            CornerIcon(
                cornerAlignment = Alignment.BottomStart,
                roundCornerSize = corner,
                background = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Icon(painterResource(R.drawable.delete_24px), "")
            }
            CornerIcon(
                cornerAlignment = Alignment.BottomEnd,
                roundCornerSize = corner,
                background = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(painterResource(R.drawable.delete_24px), "")
            }
        }
    }
}