package com.guidofe.pocketlibrary.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.guidofe.pocketlibrary.ui.theme.greentheme.GreenTheme
import com.guidofe.pocketlibrary.ui.theme.yellowtheme.YellowTheme

enum class Theme(val light: ColorScheme, val dark: ColorScheme) {
    DEFAULT(
        lightColorScheme(
            primary = Purple500,
            secondary = Teal200
        ),
        darkColorScheme(
            primary = Purple200,
            secondary = Teal200,
        )
    ),
    GREEN(GreenTheme.LightColors, GreenTheme.DarkColors),
    YELLOW(YellowTheme.LightColors, YellowTheme.DarkColors)
}

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
    dynamicColor: Boolean = false,
    theme: Theme = Theme.DEFAULT,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val extendedColors = ExtendedColors(
        green = CustomGreen,
        yellow = CustomYellow,
        red = CustomRed,
        blue = CustomBlue
    )
    val colors = if (darkTheme) {
        if (dynamicColor) dynamicDarkColorScheme(context)
        else theme.dark
    } else {
        if (dynamicColor) dynamicLightColorScheme(context)
        else theme.light
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