package com.guidofe.pocketlibrary.model.repositories.google_book

import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Media
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Progress
import java.lang.NumberFormatException
import java.net.URI

data class RawVolumeResponse(
    val title: String,
    val subtitle: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val industryIdentifiers: List<IndustryIdentifier>,
    val mainCategory: String,
    val categories: List<String>,
    val imageLinks: ImageLinks,
    val language: String,

) {
    data class IndustryIdentifier(
        val type: String,
        val identifier: String
    )

    data class ImageLinks(
        val smallThumbnail: String? = null,
        val thumbnail:String? = null,
        val small: String? = null,
        val medium: String? = null,
        val large: String? = null,
        val extraLarge: String? = null
    )



}
