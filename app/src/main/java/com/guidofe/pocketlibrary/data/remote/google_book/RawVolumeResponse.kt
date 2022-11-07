package com.guidofe.pocketlibrary.data.remote.google_book

data class RawVolumeResponse(
    val title: String?,
    val subtitle: String?,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val industryIdentifiers: List<IndustryIdentifier>?,
    val mainCategory: String?,
    val categories: List<String>?,
    val imageLinks: ImageLinks?,
    val language: String?,

) {
    data class IndustryIdentifier(
        val type: String,
        val identifier: String
    )

    data class ImageLinks(
        val smallThumbnail: String? = null,
        val thumbnail: String? = null,
        val small: String? = null,
        val medium: String? = null,
        val large: String? = null,
        val extraLarge: String? = null
    )
}
