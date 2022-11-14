package com.guidofe.pocketlibrary.repositories

import android.content.Context
import androidx.datastore.dataStore
import com.guidofe.pocketlibrary.AppSettingsSerializer
import com.guidofe.pocketlibrary.Language
import javax.inject.Inject

private val Context.dataStore by dataStore(
    fileName = "app-settings.json",
    serializer = AppSettingsSerializer
)

class DefaultDataStoreRepository @Inject constructor(
    private val context: Context
): DataStoreRepository {
    override suspend fun setLanguage(language: Language) {
        context.dataStore.updateData { it.copy(language = language) }
    }
}