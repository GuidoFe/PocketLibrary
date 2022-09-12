package com.guidofe.pocketlibrary.model.repositories.google_book

import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Media
import java.lang.NumberFormatException

data class RawArrayItemResponse (val volumeInfo: RawVolumeResponse, val saleInfo: RawSaleInfo) {
    fun toImportedBookData (): ImportedBookData {
        var industryIdentifierType: IndustryIdentifierType? = null
        var industryIdentifierValue: String? = null
        volumeInfo.industryIdentifiers.forEach {
            if (it.type == "ISBN_13") {
                industryIdentifierType = IndustryIdentifierType.valueOf(it.type)
                industryIdentifierValue = it.identifier
                return@forEach
            }
        }
        if (industryIdentifierType == null && volumeInfo.industryIdentifiers.isNotEmpty()) {
            industryIdentifierType = IndustryIdentifierType.valueOf(volumeInfo.industryIdentifiers[0].type)
            industryIdentifierValue = volumeInfo.industryIdentifiers[0].identifier
        }

        var coverUrl: String? = null
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
        val mediaType: Media = if(saleInfo.isEbook) Media.EBOOK else Media.BOOK
        var published: Int = try {
            volumeInfo.publishedDate.toInt()
        } catch (e: NumberFormatException) {
            volumeInfo.publishedDate.split('-').first().toInt();
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
            authors = volumeInfo.authors,
            genres = volumeInfo.categories
        )
    }
}