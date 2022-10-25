package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import android.net.Uri
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import com.guidofe.pocketlibrary.model.ImportedBookData
import retrofit2.http.Url

data class ViewBookImmutableData(
    val bookId: Long,
    val title: String,
    val subtitle: String?,
    val genres: List<String>,
    val authors: List<String>,
    val coverURI: Uri?,
    val description: String?,
    val publisher: String?,
    val publishedYear: Int?,
    val identifier: String?,
    val media: Media,
    val language: String?
) {
    constructor(bundle: BookBundle) : this(
        bookId = bundle.book.bookId,
        title = bundle.book.title,
        subtitle = bundle.book.subtitle,
        genres = bundle.genres.map{it.name},
        authors = bundle.authors.map{it.name},
        coverURI = bundle.book.coverURI,
        description = bundle.book.description,
        publisher = bundle.book.publisher,
        publishedYear = bundle.book.published,
        identifier = bundle.book.identifier,
        media = bundle.book.media,
        language = bundle.book.language
    )
    constructor(imported: ImportedBookData): this(
        bookId = 0L,
        title = imported.title,
        subtitle = imported.subtitle,
        genres = imported.genres,
        authors = imported.authors,
        coverURI = imported.coverUrl?.let{Uri.parse(it)},
        description = imported.description,
        publisher = imported.publisher,
        publishedYear = imported.published,
        identifier = imported.identifier,
        media = imported.media,
        language = imported.language
    )
}
