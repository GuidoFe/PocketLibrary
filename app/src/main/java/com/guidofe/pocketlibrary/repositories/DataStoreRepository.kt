package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.Language

interface DataStoreRepository {
    suspend fun setLanguage(language: Language)
}