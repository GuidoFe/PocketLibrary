package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.pages.settingspage.SettingsState
import com.guidofe.pocketlibrary.ui.pages.settingspage.SettingsState.TranslationPhase
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
    override val settingsLiveData = dataStore.settingsLiveData
    override fun setLanguage(language: Language) {
        viewModelScope.launch(Dispatchers.Main) {
            val appLocale = LocaleListCompat.forLanguageTags(language.code)
            // delay(100)
            dataStore.setLanguage(language)
            AppCompatDelegate.setApplicationLocales(appLocale)
            if(settingsLiveData.value?.allowGenreTranslation == true) {
                translateGenres(language.code)
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
                    translateGenres(it)
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

    override fun translateGenres(targetLanguageCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.translationPhase = TranslationPhase.FETCHING_GENRES
            val genresToTranslate = repo.getGenresOfDifferentLanguage(targetLanguageCode)
                .toMutableList()
            if (genresToTranslate.isEmpty()) {
                Log.w("debug", "No genres to translate")
                state.translationPhase = TranslationPhase.NO_TRANSLATING
                return@launch
            }
            state.totalGenres = genresToTranslate.size
            if (targetLanguageCode == "en") {
                state.translationPhase = TranslationPhase.UPDATING_DB
                genresToTranslate.forEachIndexed { index, genre ->
                    genresToTranslate[index] = genre.copy(name = genre.englishName, lang = "en")
                }
                repo.updateAllGenres(genresToTranslate)
                state.translationPhase = TranslationPhase.NO_TRANSLATING
                return@launch
            }

            // If target language is not English:

            state.translationPhase = TranslationPhase.DOWNLOADING_TRANSLATOR
            val targetLanguage = TranslateLanguage.fromLanguageTag(targetLanguageCode)
            if (targetLanguage == null) {
                //TODO: Manage
                Log.e("debug", "Target language is null")
                state.translationPhase = TranslationPhase.NO_TRANSLATING
                return@launch
            }
            val translationOptions = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(targetLanguage)
                .build()
            val translator = Translation.getClient(translationOptions)
            val conditions = DownloadConditions.Builder() //TODO: support allowing for data?
                .requireWifi()
                .build()
            translator.downloadModelIfNeeded(conditions)
                .addOnFailureListener {
                    //TODO: Manage failure
                    it.printStackTrace()
                    state.translationPhase = TranslationPhase.NO_TRANSLATING
                }
                .addOnSuccessListener {
                    state.translationPhase = TranslationPhase.TRANSLATING
                    state.genresTranslated = 0
                    genresToTranslate.forEachIndexed { index, genre ->
                        translator.translate(genre.englishName)
                            .addOnSuccessListener {
                                genresToTranslate[index] = genre.copy(
                                    name = it, lang = targetLanguageCode
                                )
                                state.genresTranslated += 1
                            }
                            .addOnFailureListener {
                                //TODO: Manage failure
                                Log.e("debug", "Couldn't translate ${genre.englishName}")
                                it.printStackTrace()
                                state.genresTranslated += 1
                            }
                    }
                    state.translationPhase = TranslationPhase.UPDATING_DB
                    viewModelScope.launch {
                        repo.updateAllGenres(genresToTranslate)
                        state.translationPhase = TranslationPhase.NO_TRANSLATING
                        Log.d("debug", "Translation success")
                        //TODO: Message
                    }
                }
        }
    }
}