package com.guidofe.pocketlibrary.ui.pages.editbookpage

import android.net.Uri
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.model.ImportedBookData

data class FormData(
    var title: String = "",
    var subtitle: String = "",
    var description: String = "",
    var publisher: String = "",
    var published: String = "",
    var isOwned: Boolean = true,
    var media: Media = Media.BOOK,
    var progress: Progress = Progress.NOT_READ,
    var coverUri: Uri? = null,
    var identifierType: IndustryIdentifierType = IndustryIdentifierType.ISBN_13,
    var identifier: String = "",
    var language: String = "",
    var authors: String = "",
    var note: String = "",
    var place: String = "",
    var room: String = "",
    var bookshelf: String = "",
    var genres: List<String> = listOf(),
    var isFavorite: Boolean = false
) {
    constructor(book: ImportedBookData) : this() {
        this.title = book.title
        this.subtitle = book.subtitle ?: ""
        this.description = book.description ?: ""
        this.publisher = book.publisher ?: ""
        this.published = if (book.published == null) "" else book.published.toString()
        this.media = book.media
        this.coverUri = if (book.coverUrl.isNullOrBlank()) null else Uri.parse(book.coverUrl)
        this.identifierType = book.industryIdentifierType ?: IndustryIdentifierType.ISBN_13
        this.identifier = book.identifier ?: ""
        this.language = book.language ?: ""
        this.authors = book.authors.joinToString(", ")
        this.genres = book.genres
    }

    constructor(bundle: BookBundle): this() {
        val book = bundle.book
        this.title = book.title
        this.subtitle = book.subtitle ?: ""
        this.description = book.description ?: ""
        this.publisher = book.publisher ?: ""
        this.published = (book.published?.toString()) ?: ""
        this.isOwned = book.isOwned
        this.media = book.media
        this.progress = book.progress
        this.coverUri = book.coverURI
        this.identifierType = book.industryIdentifierType
        this.identifier = book.identifier ?: ""
        this.language = book.language
        this.authors = bundle.authors.joinToString(", ")
        this.note = (bundle.note?.note) ?: ""
        this.place = (bundle.place?.name) ?: ""
        this.room = (bundle.room?.name) ?: ""
        this.bookshelf = (bundle.bookshelf?.name) ?: ""
        this.genres = bundle.genres.map {it.name}
        this.isFavorite = bundle.book.isFavorite
    }
}
