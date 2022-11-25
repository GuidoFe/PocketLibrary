package com.guidofe.pocketlibrary.viewmodels.previews

import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM

class LandingPageVMPreview : ILandingPageVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val stats: AppStats
        get() = AppStats(
            200,
            50,
            10,
            4,
        )

    override fun initStats() {
    }
}