package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

//TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    private val _appBarState: MutableStateFlow<AppBarState?>
) : ViewModel(), IMainActivityVM {
    override val appBarState: StateFlow<AppBarState?> = _appBarState.asStateFlow()
}