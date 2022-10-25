package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBasicPageVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasicPageVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
): ViewModel(), IBasicPageVM {
}