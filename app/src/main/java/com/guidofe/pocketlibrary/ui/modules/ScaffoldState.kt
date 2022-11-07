package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.*

class ScaffoldState {
    var title: String by mutableStateOf("")
    var navigationIcon: @Composable () -> Unit by mutableStateOf({})
    var actions: @Composable RowScope.() -> Unit by mutableStateOf({})
    var fab: @Composable () -> Unit by mutableStateOf({})
    var hiddenBar: Boolean by mutableStateOf(false)

    fun refreshBar(
        title: String = "",
        navigationIcon: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {}
    ) {
        this.title = title
        this.navigationIcon = navigationIcon
        this.actions = actions
    }
}
