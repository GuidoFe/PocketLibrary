package com.guidofe.pocketlibrary.ui.utils

import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import kotlinx.coroutines.CoroutineScope

suspend fun translateGenresWithState(
    code: String,
    state: TranslationDialogState,
    coroutineScope: CoroutineScope,
    repo: LocalRepository,
    onFinish: (success: Boolean) -> Unit
) {
    repo.translateGenres(
        code,
        coroutineScope,
        onPhaseChanged = { state.translationPhase = it },
        onCountedTotalGenresToUpdate = { state.totalGenres = it },
        onTranslatedGenresCountUpdate = { state.genresTranslated = it },
        onFinish = onFinish
    )
}