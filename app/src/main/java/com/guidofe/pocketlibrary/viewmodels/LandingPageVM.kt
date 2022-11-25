package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class LandingPageVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    private val repo: LocalRepository
) : ViewModel(), ILandingPageVM {

    override var stats: AppStats? by mutableStateOf(null)
        private set

    override fun initStats() {
        viewModelScope.launch(Dispatchers.IO) {
            stats = repo.getStats()
        }
    }
}