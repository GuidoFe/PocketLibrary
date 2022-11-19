package com.guidofe.pocketlibrary.repositories

import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.dataStore
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.AppSettingsSerializer
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.theme.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.lastOrNull
import java.io.File
import javax.inject.Inject

private val Context.dataStore by dataStore(
    fileName = "app-settings.json",
    serializer = AppSettingsSerializer
)

class DefaultDataStoreRepository @Inject constructor(
    private val context: Context
) : DataStoreRepository {
    override val settingsFlow: Flow<AppSettings> = context.dataStore.data
    override val COVER_DIR = "covers"

    override suspend fun getCoverDir(): File? {
        val isExternal = settingsFlow.lastOrNull()?.saveInExternal
        return if (isExternal == null)
            null
        else {
            if (isExternal)
                context.getExternalFilesDir(COVER_DIR)
            else
                context.getDir(COVER_DIR, Context.MODE_PRIVATE)
        }
    }

    override suspend fun getCover(fileName: String): File? {
        return getCoverDir()?.let { File(it, fileName) }
    }

    override suspend fun getCoverPath(fileName: String): String? {
        return getCover(fileName)?.path
    }

    override suspend fun setLanguage(language: Language) {
        context.dataStore.updateData { it.copy(language = language) }
    }

    private fun booleanToNightModeEnum(darkTheme: Boolean): Int {
        return if (darkTheme)
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(booleanToNightModeEnum(enabled))
        context.dataStore.updateData { it.copy(darkTheme = enabled) }
    }

    override suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.updateData {
            it.copy(
                dynamicColors = enabled
            )
        }
    }

    override suspend fun setTheme(theme: Theme) {
        context.dataStore.updateData {
            it.copy(
                theme = theme
            )
        }
    }

    override suspend fun setMemory(isExternal: Boolean) {
        context.dataStore.updateData {
            it.copy(
                saveInExternal = isExternal
            )
        }
    }

    override fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }
}