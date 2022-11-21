package com.guidofe.pocketlibrary.viewmodels.previews

import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM

class LandingPageVMPreview : ILandingPageVM {
    override val scaffoldState: ScaffoldState
        get() = ScaffoldState()
}