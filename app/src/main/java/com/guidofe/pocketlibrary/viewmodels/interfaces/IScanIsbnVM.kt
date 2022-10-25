package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.camera.core.ImageAnalysis
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState

interface IScanIsbnVM {
    var displayBookNotFoundDialog: Boolean
    var displayInsertIsbnDialog: Boolean
    var displayConnectionErrorDialog: Boolean
    var errorMessage: String?
    var code: String?
    fun getImageAnalysis(): ImageAnalysis
    var coverUrl: String
    val scaffoldState: ScaffoldState
}