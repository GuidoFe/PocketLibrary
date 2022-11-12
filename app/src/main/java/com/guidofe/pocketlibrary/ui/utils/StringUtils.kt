package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.utils.Constants

@Composable
fun languageName(code: String?): String {
    if (code == null)
        return "?"
    val index = Constants.languageCodes.indexOf(code.lowercase())
    val namesArray = stringArrayResource(R.array.language_names)
    return namesArray.getOrElse(index) { "?" }
}