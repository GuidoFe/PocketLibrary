package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import com.guidofe.pocketlibrary.ui.modules.bottomsheet.ModalBottomSheetState
import com.guidofe.pocketlibrary.ui.modules.bottomsheet.ModalBottomSheetValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    var bottomSheetContent: @Composable ColumnScope.() -> Unit by mutableStateOf({})
    val bottomSheetState = ModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )

    fun setBottomSheetVisibility(show: Boolean, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            if (show)
                bottomSheetState.expand()
            else
                bottomSheetState.hide()
        }
    }

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
