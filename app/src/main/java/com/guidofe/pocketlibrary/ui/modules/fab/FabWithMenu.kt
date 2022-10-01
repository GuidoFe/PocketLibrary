package com.guidofe.pocketlibrary.ui.modules.fab

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.guidofe.pocketlibrary.R

//enum class FabState {CLOSED, OPEN}

@Composable
fun FabWithMenu(
    fabIcon: Painter,
    fabIconDescription: String,
    fabColor: Color = MaterialTheme.colorScheme.primary,
    fabIconColor: Color = MaterialTheme.colorScheme.onPrimary,
    menuEntries: List<FabMenuEntry>,
    fabSize: Dp = 56.dp,
    menuIconSize: Dp = 40.dp,
    showMenuIcons: Boolean = true
) {
    var isMenuOpen by remember {mutableStateOf(false)}
    val transition = updateTransition(isMenuOpen, "transition")
    val fabAlphaAndScale by transition.animateFloat(label = "fabAlpha") { isOpen -> if (isOpen) 0f else 1f }
    val menuAlpha by transition.animateFloat(label = "menuAlpha") { isOpen -> if (isOpen) 1f else 0f }
    val menuScale by transition.animateFloat(label = "menuScale") { isOpen -> if (isOpen) 1f else 0.8f }
    val animatedFabColor by transition.animateColor(label = "fabColor") {
            isOpen -> if (isOpen) MaterialTheme.colorScheme.surface else fabColor
    }
    Box(
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = {
                isMenuOpen = !isMenuOpen
            },
            shape = RoundedCornerShape(100 * fabAlphaAndScale),
            containerColor = animatedFabColor,
            contentColor = fabIconColor,
            modifier = Modifier
                .size(fabSize)
                .graphicsLayer {
                    scaleX = 2 - fabAlphaAndScale
                    scaleY = 2 - fabAlphaAndScale
                    //alpha = fabAlphaAndScale
                    transformOrigin = TransformOrigin(1f, 1f)
                }
                .shadow(0.dp)
        ) {
            Icon(
                painter = fabIcon,
                contentDescription = fabIconDescription,
                modifier = Modifier
            )
        }
        if(isMenuOpen) {
            Popup(
                alignment = Alignment.BottomEnd,
                properties = PopupProperties(dismissOnClickOutside = true, clippingEnabled = false),
                onDismissRequest = {
                    isMenuOpen = false
                }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .alpha(menuAlpha)
                        .scale(menuScale)
                        //.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                    ) {
                        menuEntries.forEach { item ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .height(45.dp)
                                    .clickable(enabled = isMenuOpen) {
                                        item.onClick()
                                        isMenuOpen = !isMenuOpen
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                            ) {
                                if (showMenuIcons)
                                    Icon(item.icon, item.label)
                                Text(
                                    item.label,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewFabWithMenu() {
    FabWithMenu(
        fabIcon = painterResource(R.drawable.ic_baseline_add_24),
        fabIconDescription = "Add",
        showMenuIcons = true,
        menuEntries = listOf(
            FabMenuEntry("Test 1", painterResource(R.drawable.ic_baseline_star_24)),
            FabMenuEntry("Test 2", painterResource(R.drawable.ic_baseline_book_24)),
            FabMenuEntry("Very long Test", painterResource(R.drawable.ic_baseline_chart_outlined_24)),
            FabMenuEntry("Test 4", painterResource(R.drawable.ic_baseline_borrowed_24))
        ))
}