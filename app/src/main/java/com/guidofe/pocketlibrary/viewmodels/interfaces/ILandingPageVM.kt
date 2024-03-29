package com.guidofe.pocketlibrary.viewmodels.interfaces

import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState

interface ILandingPageVM {
    val scaffoldState: ScaffoldState
    val stats: AppStats?
    fun initStats()
}