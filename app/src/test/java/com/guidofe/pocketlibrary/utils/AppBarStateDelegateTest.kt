package com.guidofe.pocketlibrary.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.google.common.truth.Truth.assertThat
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Test

class AppBarStateDelegateTest {
    lateinit var appBarStateDelegate: AppBarStateDelegate
    lateinit var appBarState: AppBarState
    @Before
    fun setUp() {
        appBarState = AppBarState(
            title = "Title"
        )
        appBarStateDelegate = AppBarStateDelegate(MutableStateFlow(appBarState))
    }

    @Test
    fun `set a new AppBarState`() {
        val newAppBarState = AppBarState(title = "New title")
        appBarStateDelegate.setAppBarContent(newAppBarState)
        assertThat(appBarStateDelegate.getValue()?.title).isEqualTo("New title")
    }

    @Test
    fun `change title successfully`() {
        appBarStateDelegate.setTitle("New title")
        assertThat(appBarState.title).isEqualTo("New title")
    }

    @Test
    fun `change navigation icon successfully`() {
        val newNavigationIcon: @Composable () -> Unit = @Composable () {}
        appBarStateDelegate.setNavigationIcon(newNavigationIcon)
        assertThat(appBarState.navigationIcon.toString()).isEqualTo(newNavigationIcon.toString())
    }

    @Test
    fun `change actions successfully`() {
        val newActions: @Composable RowScope.() -> Unit = @Composable () {}
        appBarStateDelegate.setActions(newActions)
        assertThat(appBarState.actions.toString()).isEqualTo(newActions.toString())
    }
}