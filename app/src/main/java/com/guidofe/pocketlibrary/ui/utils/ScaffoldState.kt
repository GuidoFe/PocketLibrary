package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

@OptIn(ExperimentalMaterial3Api::class)
class ScaffoldState {
    var title: @Composable () -> Unit by mutableStateOf({})
    var navigationIcon: @Composable () -> Unit by mutableStateOf({})
    var actions: @Composable RowScope.() -> Unit by mutableStateOf({})
    var fab: @Composable () -> Unit by mutableStateOf({})
    var hiddenBar: Boolean by mutableStateOf(false)
    var topAppBar: @Composable () -> Unit by mutableStateOf({})
    var nestedScrollCollection: NestedScrollConnection by mutableStateOf(
        object : NestedScrollConnection {}
    )
    fun refreshBar(
        title: @Composable () -> Unit = {},
        navigationIcon: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {}
    ) {
        hiddenBar = false
        this.title = title
        this.navigationIcon = navigationIcon
        this.actions = actions
    }
}
