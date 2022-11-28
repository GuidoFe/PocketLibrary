package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.settingspage.SettingsState
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISettingsVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsVM @Inject constructor(
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
    private val dataStore: DataStoreRepository,
    private val repo: LocalRepository
) : ViewModel(), ISettingsVM {
    override val state = SettingsState()
    override val translationState = TranslationDialogState()
    override val settingsLiveData = dataStore.settingsLiveData
    override fun setLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.Main) {
            val appLocale = LocaleListCompat.forLanguageTags(language.code)
            // delay(100)
            dataStore.setLanguage(language)
            AppCompatDelegate.setApplicationLocales(appLocale)
            if(settingsLiveData.value?.allowGenreTranslation == true) {
                startTranslation(language.code)
            }
        }
    }

    private fun startTranslation(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.translateGenres(
                code,
                viewModelScope,
                onPhaseChanged = {translationState.translationPhase = it},
                onCountedTotalGenresToUpdate = {translationState.totalGenres = it},
                onTranslatedGenresCountUpdate = {translationState.genresTranslated = it}
            )
        }
    }
    override fun getCurrentLanguageName(): String {
        val locale = AppCompatDelegate.getApplicationLocales().getFirstMatch(
            Language.values().map { it.code }.toList().toTypedArray()
        )
        return locale?.displayLanguage?.replaceFirstChar { it.uppercase() } ?: "English"
    }

    override fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setDynamicColors(enabled)
        }
    }

    override fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setDarkTheme(enabled)
        }
    }

    override fun setTheme(theme: Theme) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setTheme(theme)
        }
    }

    override fun setGenreTranslation(translate: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStore.setGenreTranslation(translate)
        }
        viewModelScope.launch(Dispatchers.IO) {
            if (translate) {
                settingsLiveData.value?.language?.code?.let {
                    startTranslation(it)
                }
            } else {
                val modelManager = RemoteModelManager.getInstance()
                modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
                    .addOnFailureListener {
                        Log.e("debug", "Couldn't get downloaded models to delete")
                        it.printStackTrace()
                    }
                    .addOnSuccessListener {
                        for (model in it) {
                            modelManager.deleteDownloadedModel(model)
                                .addOnSuccessListener {
                                    Log.d("debug", "Model ${model.modelName} deleted")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("debug", "Couldn't delete ${model.modelName}")
                                    e.printStackTrace()
                                }
                        }
                    }
            }
        }
    }

    override fun setMemoryAndTransferFiles(isExternal: Boolean, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val oldFolder = dataStore.getCoverDir(!isExternal)
                if (oldFolder == null) {
                    Log.e("debug", "oldFolder is null")
                    callback(false)
                    return@launch
                }
                val oldFiles = oldFolder.listFiles()
                state.totalFiles = oldFiles?.size ?: 0
                state.movedFiles = 0
                oldFiles?.forEach { oldFile ->
                    val newFile = dataStore.getCoverFile(oldFile.name, isExternal)
                    if (newFile == null) {
                        Log.e("debug", "New file is null")
                        callback(false)
                        return@launch
                    } else {
                        oldFile.copyTo(newFile)
                        state.movedFiles += 1
                    }
                }
                oldFolder.listFiles()?.forEach {
                    it.delete()
                }
                dataStore.setMemory(isExternal)
                callback(true)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }

    override val hasExternalStorage: Boolean
        get() = dataStore.isExternalStorageWritable()
}