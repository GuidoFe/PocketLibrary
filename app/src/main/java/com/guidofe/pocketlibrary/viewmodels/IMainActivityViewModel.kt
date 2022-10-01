package com.guidofe.pocketlibrary.viewmodels

import com.guidofe.pocketlibrary.ui.modules.AppBarState
import kotlinx.coroutines.flow.StateFlow

interface IMainActivityViewModel {
    val appBarState: StateFlow<AppBarState?>
}