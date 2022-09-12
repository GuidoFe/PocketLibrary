package com.guidofe.pocketlibrary.ui.modules.fab

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MultiActionFab(
    fabIcon: Painter,
    fabIconDescription: String,
    fabColor: Color = MaterialTheme.colors.primary,
    fabIconColor: Color = MaterialTheme.colors.onPrimary,
    menuEntries: List<FabMenuEntry>,
    fabSize: Dp = 56.dp,
    miniFabSize: Dp = 40.dp,
    showMiniFabText: Boolean = true
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val transition = updateTransition(isMenuOpen, "transition")
    val fabRotation by transition.animateFloat(label="FabRotation") { isOpen ->
        if(isOpen) 45f else 0f
    }
    val elevation = 5.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .offset(x = (miniFabSize-fabSize)/2)
        ) {
            menuEntries.forEachIndexed { index, entry ->
                val delay = (menuEntries.size - index - 1) * 100
                val miniFabScaleAndAlpha by transition.animateFloat(
                    label = "miniFab${index}Scale",
                    transitionSpec = { tween(delayMillis = delay) }
                ) { isOpen ->
                    if (isOpen) 1f else 0f
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showMiniFabText) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colors.surface,
                            elevation = elevation,
                            modifier = Modifier
                                //.alpha(miniFabScaleAndAlpha)
                                .graphicsLayer(
                                    scaleX = miniFabScaleAndAlpha,
                                    scaleY = miniFabScaleAndAlpha,
                                    transformOrigin = TransformOrigin(1f, 0.5f),
                                )
                                .scale(miniFabScaleAndAlpha)
                                .clickable {
                                    entry.onClick()
                                }
                        ) {
                            Text(
                                entry.label,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = { entry.onClick() },
                        modifier = Modifier
                            .size(miniFabSize)
                            .scale(miniFabScaleAndAlpha)
                    ) {
                        Icon(painter = entry.icon, contentDescription = entry.label)
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                isMenuOpen = !isMenuOpen
            },
            backgroundColor = fabColor,
            contentColor = fabIconColor,
            modifier = Modifier
                .size(fabSize)
                .rotate(fabRotation)
        ) {
            Icon(
                painter = fabIcon,
                contentDescription = fabIconDescription,
                modifier = Modifier
            )
        }
    }
}
