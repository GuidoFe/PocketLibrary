package com.guidofe.pocketlibrary.viewmodels.previews

import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM

class LandingPageVMPreview : ILandingPageVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
    override val stats: AppStats
        get() = AppStats(
            100,
            20,
            10,
            40,
            3,
            1,
            2,
            10,
            listOf(PreviewUtils.exampleBookBundle, PreviewUtils.exampleBookBundle)
        )

    override fun initStats() {
    }
}