package com.guidofe.pocketlibrary.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportedBookData(
    val externalId: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val published: Int? = null,
    val coverUrl: String? = null,
    val identifier: String? = null,
    val isEbook: Boolean = false,
    val language: String? = null,
    val authors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val pageCount: Int? = null
) : Parcelable
