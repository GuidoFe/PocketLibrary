package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.utils.TranslationPhase

class TranslationDialogState() {
    var totalGenres by mutableStateOf(0)
    var genresTranslated by mutableStateOf(0)
    var translationPhase by mutableStateOf(TranslationPhase.NO_TRANSLATING)
}

@Composable
fun TranslationDialog(state: TranslationDialogState) {
    if (state.translationPhase == TranslationPhase.NO_TRANSLATING)
        return
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .clip(AlertDialogDefaults.shape)
                .background(AlertDialogDefaults.containerColor)
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.translation_in_progress),
                style = MaterialTheme.typography.titleMedium
            )
            Text(stringResource(R.string.dialog_translation_text))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                if (
                    state.translationPhase != TranslationPhase.TRANSLATING ||
                    state.totalGenres == 0
                ) {
                    Text(
                        stringResource(
                            when (state.translationPhase) {
                                TranslationPhase.DOWNLOADING_TRANSLATOR ->
                                    R.string.downloading_translator_dots
                                TranslationPhase.FETCHING_GENRES ->
                                    R.string.fetching_genres_dots
                                TranslationPhase.UPDATING_DB ->
                                    R.string.updating_db_dots
                                TranslationPhase.TRANSLATING ->
                                    R.string.translating_dots
                                else -> R.string.EMPTY_STRING
                            }
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                    CircularProgressIndicator()
                } else {
                    Text(
                        "${state.genresTranslated}/${state.totalGenres}",
                        modifier = Modifier.padding(10.dp)
                    )
                    LinearProgressIndicator(
                        progress = state.genresTranslated.toFloat() / state.totalGenres,
                    )
                }
            }
        }
    }
}