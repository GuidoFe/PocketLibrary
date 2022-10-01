package com.guidofe.pocketlibrary.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import kotlinx.coroutines.flow.MutableStateFlow

class AppBarStateDelegate(
    private var appBar: MutableStateFlow<AppBarState?>) {
    fun getValue(): AppBarState? {
        return appBar.value
    }
    fun setAppBarContent(newState: AppBarState) {
        appBar.value = newState
    }
    fun setTitle(title: String) {
        appBar.value?.title = title
    }
    fun setNavigationIcon(navigationIcon: @Composable () -> Unit) {
        appBar.value?.navigationIcon = navigationIcon
    }
    fun setActions(actions: @Composable RowScope.() -> Unit) {
        appBar.value?.actions = actions
    }
}