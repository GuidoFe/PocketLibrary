package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media

class FormattingUtils {
    companion object {
        @Composable
        fun bookMediaToString(media: Media): String {
            return when (media) {
                Media.BOOK -> stringResource(R.string.book)
                Media.EBOOK -> stringResource(R.string.ebook)
            }
        }
    }
}