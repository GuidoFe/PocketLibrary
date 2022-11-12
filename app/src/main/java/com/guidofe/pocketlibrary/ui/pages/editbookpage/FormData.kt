package com.guidofe.pocketlibrary.ui.pages.editbookpage

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.model.ImportedBookData

class FormData() {
    var title: String by mutableStateOf("")
    var subtitle: String by mutableStateOf("")
    var description: String by mutableStateOf("")
    var publisher: String by mutableStateOf("")
    var published: String by mutableStateOf("")
    var media: Media by mutableStateOf(Media.BOOK)
    var coverUri: Uri? by mutableStateOf(null)
    var identifier: String by mutableStateOf("")
    var language: String by mutableStateOf("")
    var authors: String by mutableStateOf("")
    var genres: List<String> by mutableStateOf(listOf())
    var genreInput: String by mutableStateOf("")
    var existingGenres: List<String> by mutableStateOf(listOf())
    constructor(
        title: String = "",
        subtitle: String = "",
        description: String = "",
        publisher: String = "",
        published: Int? = null,
        media: Media = Media.BOOK,
        coverUri: Uri? = null,
        identifier: String = "",
        language: String = "",
        authors: String = "",
        genres: List<String> = listOf(),

    ) : this() {
        this.title = title
        this.subtitle = subtitle
        this.description = description
        this.publisher = publisher
        this.published = published?.toString() ?: ""
        this.media = media
        this.coverUri = coverUri
        this.identifier = identifier
        this.language = language
        this.authors = authors
        this.genres = genres
    }

    constructor(bundle: BookBundle) : this() {
        val book = bundle.book
        this.title = book.title
        this.subtitle = book.subtitle ?: ""
        this.description = book.description ?: ""
        this.publisher = book.publisher ?: ""
        this.published = (book.published?.toString()) ?: ""
        this.media = book.media
        this.coverUri = book.coverURI
        this.identifier = book.identifier ?: ""
        this.language = book.language ?: ""
        this.authors = bundle.authors.joinToString(", ")
        this.genres = bundle.genres.map { it.name }
    }

    constructor(book: ImportedBookData) : this() {
        this.title = book.title
        this.subtitle = book.subtitle ?: ""
        this.description = book.description ?: ""
        this.publisher = book.publisher ?: ""
        this.published = (book.published?.toString()) ?: ""
        this.media = book.media
        this.coverUri = book.coverUrl?.let { Uri.parse(it) }
        this.identifier = book.identifier ?: ""
        this.language = book.language ?: ""
        this.authors = book.authors.joinToString(", ")
        this.genres = book.genres
    }
}
