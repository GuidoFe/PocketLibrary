package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.pages.settingspage.SettingsState
import com.guidofe.pocketlibrary.ui.theme.Theme
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.translateGenresWithState
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
            if (settingsLiveData.value?.allowGenreTranslation == true) {
                translateGenresWithState(language.code, translationState, viewModelScope, repo) {}
            }
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
                    Log.d("debug", "Translating to lang $it...")
                    translateGenresWithState(it, translationState, viewModelScope, repo) {}
                }
            } else {
                val genres = repo.getGenresOfDifferentLanguage("en")
                val updatedGenres = genres.filter { it.englishName != null }.map {
                    it.copy(name = it.englishName!!, lang = "en")
                }
                repo.updateAllGenres(updatedGenres)
                repo.deleteDownloadedTranslators()
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