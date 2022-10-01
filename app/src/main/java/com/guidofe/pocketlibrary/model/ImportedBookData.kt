package com.guidofe.pocketlibrary.model

import android.os.Parcelable
import com.guidofe.pocketlibrary.data.local.library_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.data.local.library_db.entities.Media
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedBookData(
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val published: Int? = null,
    val coverUrl: String? = null,
    val industryIdentifierType: IndustryIdentifierType? = null,
    val identifier: String? = null,
    val media: Media = Media.BOOK,
    val language: String? = null,
    val authors: List<String> = listOf(),
    val genres: List<String> = listOf()
): Parcelable

