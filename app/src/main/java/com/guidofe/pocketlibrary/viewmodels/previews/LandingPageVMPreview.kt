package com.guidofe.pocketlibrary.viewmodels.previews

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM

class LandingPageVMPreview(
    private val bookList: List<BookBundle> = List(2) { PreviewUtils.exampleBookBundle }
) : ILandingPageVM {
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
            bookList
        )

    override fun initStats() {
    }
}