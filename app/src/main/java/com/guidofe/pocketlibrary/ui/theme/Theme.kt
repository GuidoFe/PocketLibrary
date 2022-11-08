package com.guidofe.pocketlibrary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// TODO: Support material3 system colors
private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    secondary = Teal200
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    secondary = Teal200
)

@Immutable
data class ExtendedColors(
    val green: Color,
    val yellow: Color,
    val red: Color,
    val blue: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        green = Color.Unspecified,
        yellow = Color.Unspecified,
        red = Color.Unspecified,
        blue = Color.Unspecified
    )
}

@Composable
fun PocketLibraryTheme(
    darkTheme: Boolean = false,
    // darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = ExtendedColors(
        green = CustomGreen,
        yellow = CustomYellow,
        red = CustomRed,
        blue = CustomBlue
    )
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object ExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}