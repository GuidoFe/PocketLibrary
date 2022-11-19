package com.guidofe.pocketlibrary.viewmodels

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IMainActivityVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: check if it's necessary
@HiltViewModel
class MainActivityVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository
) : ViewModel(), IMainActivityVM {
    val settingsFlow = dataStore.settingsFlow
    fun initializeApp(context: Context, onCompleted: () -> Unit) {
        viewModelScope.launch {
            context.getDir(dataStore.COVER_DIR, Context.MODE_PRIVATE)
        }
    }
}