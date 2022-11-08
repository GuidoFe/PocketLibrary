package com.guidofe.pocketlibrary.data.remote.google_book

import android.util.Log
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.model.ImportedBookData

data class RawListItemResponse(
    val id: String,
    val volumeInfo: RawVolumeResponse,
    val saleInfo: RawSaleInfo
) {
    fun toImportedBookData(): ImportedBookData? {
        if (this.volumeInfo.title == null)
            return null
        var code13: String? = null
        var code10: String? = null
        var issn: String? = null
        volumeInfo.industryIdentifiers?.let {
            volumeInfo.industryIdentifiers.forEach {
                when (it.type) {
                    "ISBN_13" -> {
                        code13 = it.identifier
                        return@forEach
                    }
                    "ISBN_10" -> code10 = it.identifier
                    "ISSN" -> issn = it.identifier
                }
            }
        }

        var coverUrl: String? = null
        if (volumeInfo.imageLinks != null) {
            if (volumeInfo.imageLinks.large != null)
                coverUrl = volumeInfo.imageLinks.large
            else if (volumeInfo.imageLinks.medium != null)
                coverUrl = volumeInfo.imageLinks.medium
            else if (volumeInfo.imageLinks.small != null)
                coverUrl = volumeInfo.imageLinks.small
            else if (volumeInfo.imageLinks.thumbnail != null)
                coverUrl = volumeInfo.imageLinks.thumbnail
            else if (volumeInfo.imageLinks.smallThumbnail != null)
                coverUrl = volumeInfo.imageLinks.smallThumbnail
            if (coverUrl != null) {
                coverUrl = coverUrl.replace("http:", "https:", true)
            }
        }
        val mediaType: Media = if (saleInfo.isEbook) Media.EBOOK else Media.BOOK
        val published: Int? = if (volumeInfo.publishedDate == null) null else {
            try {
                volumeInfo.publishedDate.toInt()
            } catch (e: NumberFormatException) {
                try {
                    volumeInfo.publishedDate
                        .replace("*", "")
                        .split('-')
                        .first()
                        .toInt()
                } catch (e: Exception) {
                    Log.w("debug", "Data parsing failed for ${volumeInfo.publishedDate}")
                    null
                }
            }
        }
        return ImportedBookData(
            externalId = id,
            title = volumeInfo.title,
            subtitle = volumeInfo.subtitle,
            description = volumeInfo.description,
            publisher = volumeInfo.publisher,
            published = published,
            coverUrl = coverUrl,
            identifier = code13 ?: code10 ?: issn,
            media = mediaType,
            language = volumeInfo.language,
            authors = volumeInfo.authors ?: listOf(),
            genres = volumeInfo.categories ?: listOf(),
            pageCount = volumeInfo.pageCount
        )
    }
}