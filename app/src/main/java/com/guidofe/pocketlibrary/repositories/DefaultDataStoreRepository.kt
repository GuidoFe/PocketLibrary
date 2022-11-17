package com.guidofe.pocketlibrary.repositories

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.dataStore
import com.guidofe.pocketlibrary.AppSettings
import com.guidofe.pocketlibrary.AppSettingsSerializer
import com.guidofe.pocketlibrary.Language
import com.guidofe.pocketlibrary.ui.theme.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.dataStore by dataStore(
    fileName = "app-settings.json",
    serializer = AppSettingsSerializer
)

class DefaultDataStoreRepository @Inject constructor(
    private val context: Context
) : DataStoreRepository {
    override val settingsFlow: Flow<AppSettings> = context.dataStore.data
    override suspend fun setLanguage(language: Language) {
        context.dataStore.updateData { it.copy(languageName = language.toString()) }
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
                themeName = theme.toString()
            )
        }
    }
}