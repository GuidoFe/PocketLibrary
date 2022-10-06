package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import kotlinx.coroutines.flow.MutableStateFlow

interface IDestinationViewModel {
    val appBarDelegate: AppBarStateDelegate
}