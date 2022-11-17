package com.guidofe.pocketlibrary

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,
    val followSystem: Boolean = false,
    val dynamicColors: Boolean = false,
    val darkTheme: Boolean = false,
    val saveInExternal: Boolean = false
)

@Serializable
enum class Language(val localizedName: String, val code: String) {
    ENGLISH("English", "en"),
    ITALIAN("Italiano", "it");
}