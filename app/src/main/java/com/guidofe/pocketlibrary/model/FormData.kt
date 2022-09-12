package com.guidofe.pocketlibrary.model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.*

data class FormData(
    val title: MutableState<String> = mutableStateOf(""),
    val subtitle: MutableState<String> = mutableStateOf(""),
    val description: MutableState<String> = mutableStateOf(""),
    val publisher: MutableState<String> = mutableStateOf(""),
    val published: MutableState<Int?> = mutableStateOf<Int?>(null),
    val isOwned: MutableState<Boolean> = mutableStateOf(false),
    val media: MutableState<Media> = mutableStateOf(Media.BOOK),
    val progress: MutableState<Progress> = mutableStateOf(Progress.NOT_READ),
    val coverUri: MutableState<Uri?> = mutableStateOf(null),
    val identifierType: MutableState<IndustryIdentifierType> = mutableStateOf(
        IndustryIdentifierType.ISBN_13),
    val identifier: MutableState<String> = mutableStateOf(""),
    val language: MutableState<String> = mutableStateOf("en"),
    val authors: SnapshotStateList<Author> = mutableStateListOf(),
    val note: MutableState<String> = mutableStateOf("")
) {
    fun clear() {
        title.value = ""
        subtitle.value = ""
        description.value = ""
        publisher.value = ""
        published.value = null
        isOwned.value = false
        media.value = Media.BOOK
        progress.value = Progress.NOT_READ
        coverUri.value = null
        identifierType.value = IndustryIdentifierType.ISBN_13
        identifier.value = ""
        language.value = "en"
        authors.clear()
        note.value = ""
    }
}