package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class AppBarState(
    var title: String = "",
    var navigationIcon: @Composable () -> Unit = {},
    var actions: @Composable RowScope.() -> Unit = {}
)
