package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class CustomSnackbarVisuals(
    override val message: String,
    val isError: Boolean = false,
    override val actionLabel: String? = null,
) : SnackbarVisuals {
    override val withDismissAction: Boolean = isError
    override val duration: SnackbarDuration
        get() = if (isError)
            SnackbarDuration.Long
        else
            SnackbarDuration.Short
}