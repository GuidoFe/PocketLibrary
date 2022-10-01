package com.guidofe.pocketlibrary.data.remote.google_book

import com.guidofe.pocketlibrary.data.local.library_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.model.ImportedBookData

data class RawArrayItemResponse (val volumeInfo: RawVolumeResponse, val saleInfo: RawSaleInfo) {
    fun toImportedBookData (): ImportedBookData {
        var industryIdentifierType: IndustryIdentifierType? = null
        var industryIdentifierValue: String? = null
        volumeInfo.industryIdentifiers?.let {
            volumeInfo.industryIdentifiers.forEach {
                if (it.type == "ISBN_13") {
                    industryIdentifierType = IndustryIdentifierType.valueOf(it.type)
                    industryIdentifierValue = it.identifier
                    return@forEach
                }
            }
            if (industryIdentifierType == null && volumeInfo.industryIdentifiers.isNotEmpty()) {
                industryIdentifierType =
                    IndustryIdentifierType.valueOf(volumeInfo.industryIdentifiers[0].type)
                industryIdentifierValue = volumeInfo.industryIdentifiers[0].identifier
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
        var published: Int? = if (volumeInfo.publishedDate == null) null else {
            try {
                volumeInfo.publishedDate.toInt()
            } catch (e: NumberFormatException) {
                volumeInfo.publishedDate.split('-').first().toInt();
            }
        }
        return ImportedBookData(
            title = volumeInfo.title,
            subtitle = volumeInfo.subtitle,
            description = volumeInfo.description,
            publisher = volumeInfo.publisher,
            published = published,
            coverUrl = coverUrl,
            industryIdentifierType = industryIdentifierType,
            identifier = industryIdentifierValue,
            media = mediaType,
            language = volumeInfo.language,
            authors = volumeInfo.authors ?: listOf(),
            genres = volumeInfo.categories ?: listOf()
        )
    }
}