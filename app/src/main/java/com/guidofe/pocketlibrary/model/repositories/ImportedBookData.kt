package com.guidofe.pocketlibrary.model.repositories

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.IndustryIdentifierType
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Media
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Progress
import kotlinx.parcelize.Parcelize
import java.net.URI
@Parcelize
data class ImportedBookData(
    val title: String,
    val subtitle: String?,
    val description: String?,
    val publisher: String?,
    val published: Int?,
    val coverUrl: String? = null,
    val industryIdentifierType: IndustryIdentifierType?,
    val identifier: String?,
    val media: Media = Media.BOOK,
    val language: String = "en",
    val authors: List<String>,
    val genres: List<String>
): Parcelable

