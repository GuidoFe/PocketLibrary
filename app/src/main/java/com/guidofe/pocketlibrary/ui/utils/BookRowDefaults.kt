package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
object BookRowDefaults {
    val horizontalPadding: Dp = 10.dp
    val verticalPadding: Dp = 10.dp
    val coverTextDistance: Dp = 10.dp
    val titleStyle
        @Composable
        get() = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
        )
    val subtitleStyle
        @Composable
        get() = MaterialTheme.typography.bodyMedium
    val authorStyle
        @Composable
        get() = MaterialTheme.typography.labelLarge.copy(
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Light
        )
    val buttonLabelStyle
        @Composable
        get() = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    val buttonTextStyle
        @Composable
        get() = MaterialTheme.typography.labelMedium
}
