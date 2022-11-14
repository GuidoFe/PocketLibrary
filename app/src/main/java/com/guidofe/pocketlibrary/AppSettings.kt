package com.guidofe.pocketlibrary

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,
    val followSystem: Boolean = false,
    val dynamicTheme: Boolean = false,
    val darkTheme: Boolean = false,
    val saveInExternal: Boolean = false
)

enum class Language {ENGLISH, ITALIAN}