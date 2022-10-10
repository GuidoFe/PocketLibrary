package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.ui.modules.AppBarState
import kotlinx.coroutines.flow.StateFlow

interface IMainActivityVM {
    val appBarState: StateFlow<AppBarState?>
}