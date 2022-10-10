package com.guidofe.pocketlibrary.viewmodels.interfaces

import androidx.camera.core.ImageAnalysis

interface IScanIsbnVM: IDestinationVM {
    var displayBookNotFoundDialog: Boolean
    var displayInsertIsbnDialog: Boolean
    var displayConnectionErrorDialog: Boolean
    var errorMessage: String?
    var code: String?
    fun getImageAnalysis(): ImageAnalysis
    var coverUrl: String
}