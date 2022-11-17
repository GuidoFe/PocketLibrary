package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.theme.Theme

private object TriangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            lineTo(size.width, 0f)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun ThemeTile(
    theme: Theme,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val selectionOffset by animateDpAsState(targetValue = if (isSelected) (-5).dp else 0.dp)
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(theme.dark.tertiary, MaterialTheme.shapes.small)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = selectionOffset, y = selectionOffset)
                .clip(MaterialTheme.shapes.small)
                .background(theme.light.primary)
                .clickable { onClick() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(TriangleShape)
                    .background(theme.dark.primary)
            )
        }
    }
}

@Composable
fun ThemeSelector(
    themes: List<Theme>,
    currentTheme: Theme,
    onDismiss: () -> Unit,
    onClick: (Theme) -> Unit,
    onSubmit: (Theme) -> Unit
) {
    var selectedTheme: Theme by remember { mutableStateOf(currentTheme) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onSubmit(selectedTheme) }) {
                Text(stringResource(R.string.ok_label))
            }
        },
        text = {
            FlowRow(
                mainAxisSpacing = 10.dp,
                crossAxisSpacing = 10.dp
            ) {
                for (theme in themes) {
                    ThemeTile(theme, selectedTheme == theme) {
                        selectedTheme = theme
                        onClick(theme)
                    }
                }
            }
        }
    )
}