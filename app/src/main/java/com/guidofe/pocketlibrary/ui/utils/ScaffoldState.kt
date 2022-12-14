package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
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
    var scrollBehavior: TopAppBarScrollBehavior by mutableStateOf(object : TopAppBarScrollBehavior {
        override val flingAnimationSpec: DecayAnimationSpec<Float>?
            get() = null
        override val isPinned: Boolean
            get() = true
        override val nestedScrollConnection: NestedScrollConnection
            get() = object : NestedScrollConnection {}
        override val snapAnimationSpec: AnimationSpec<Float>?
            get() = null
        override val state: TopAppBarState
            get() = TopAppBarState(-Float.MAX_VALUE, 0f, 0f)
    })

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
