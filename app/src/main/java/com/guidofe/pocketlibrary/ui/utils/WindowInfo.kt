package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowType { COMPAT, MEDIUM, EXTENDED }

data class WindowInfo(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType
) {
    fun isBottomAppBarLayout(): Boolean {
        return this.screenWidthInfo == WindowType.COMPAT
    }

    fun isBottomSheetLayout(): Boolean {
        return this.screenHeightInfo > WindowType.COMPAT && this.screenWidthInfo > WindowType.COMPAT
    }
}

@Composable
fun rememberWindowInfo(): WindowInfo {
    val config = LocalConfiguration.current
    val w = config.screenWidthDp
    val h = config.screenHeightDp
    return WindowInfo(
        screenWidth = w.dp,
        screenHeight = h.dp,
        screenWidthInfo = when {
            w < 600 -> WindowType.COMPAT
            w < 840 -> WindowType.MEDIUM
            else -> WindowType.EXTENDED
        },
        screenHeightInfo = when {
            h < 480 -> WindowType.COMPAT
            h < 900 -> WindowType.MEDIUM
            else -> WindowType.EXTENDED
        }
    )
}