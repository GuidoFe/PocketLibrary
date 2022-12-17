package com.guidofe.pocketlibrary

import com.guidofe.pocketlibrary.ui.theme.Theme
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalTime

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,
    val followSystem: Boolean = false,
    val dynamicColors: Boolean = false,
    val darkTheme: Boolean = false,
    val saveInExternal: Boolean = false,
    val theme: Theme = Theme.DEFAULT,
    val allowGenreTranslation: Boolean = false,
    val defaultShowNotificationNDaysBeforeDue: Int = 3,
    val defaultEnableNotification: Boolean = true,
    @Serializable(with = LocalTimeSerializer::class)
    val defaultTimeToShowNotification: LocalTime = LocalTime.parse("08:00")
)

@Serializable
enum class Language(val localizedName: String, val code: String) {
    ENGLISH("English", "en"),
    ITALIAN("Italiano", "it");
}

@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer : KSerializer<LocalTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString())
    }
}