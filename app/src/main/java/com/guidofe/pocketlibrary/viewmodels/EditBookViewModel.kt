package com.guidofe.pocketlibrary.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.repositories.ImportedBookData
import com.guidofe.pocketlibrary.model.FormData
import com.guidofe.pocketlibrary.model.repositories.local_db.AppDatabase
import com.guidofe.pocketlibrary.model.repositories.local_db.BookBundle
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Author
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Genre
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Note
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.utils.getUri
import com.guidofe.pocketlibrary.utils.nullIfEmptyOrBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterialApi
@HiltViewModel
class EditBookViewModel @Inject constructor(
    db: AppDatabase,
    appContext: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val defaultCoverUri:Uri = appContext.resources.getUri(R.drawable.ic_baseline_book_24)
    lateinit var formData: FormData
        private set
    //var formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
    //    private set
    private var currentId = 0L
    @Inject
    lateinit var db: AppDatabase
    init {
        val navArgs = EditBookPageDestination.argsFrom(savedStateHandle)
        when {
            navArgs.bookBundle != null -> initialiseFormDataFromDatabase(navArgs.bookBundle)
            navArgs.importedBookData != null -> initializeFormDataFromImportedBook(navArgs.importedBookData)
            else -> formData = FormData(coverUri = mutableStateOf(defaultCoverUri))
        }
    }
    fun initializeFormDataFromImportedBook(book: ImportedBookData) {
        formData = FormData()
        viewModelScope.launch(Dispatchers.IO) {
            currentId = 0L
            Log.d("Test", "Initializing from imported data...")
            formData = FormData()
            formData.title.value = book.title
            formData.subtitle.value = book.subtitle ?: ""
            formData.description.value = book.description ?: ""
            formData.publisher.value = book.publisher ?: ""
            formData.published.value = book.published
            formData.media.value = book.media
            if (book.industryIdentifierType != null && !book.identifier.isNullOrBlank()) {
                formData.identifierType.value = book.industryIdentifierType
                formData.identifier.value = book.identifier
            }
            formData.language.value = book.language
            val existingAuthors = db.authorDao().getExistingAuthors(book.authors)
            val existingAuthorsNames = existingAuthors.map { author -> author.name }
            formData.authors.addAll(existingAuthors)
            book.authors.forEach { authorName ->
                if (!existingAuthorsNames.contains(authorName))
                    formData.authors.add(Author(0L, authorName))
            }
            formData.coverUri.value = Uri.parse(book.coverUrl)
        }
    }

    fun initialiseFormDataFromDatabase(
        bookBundle: BookBundle
    ) {
        formData = FormData()
        val book = bookBundle.book;
        val authors = bookBundle.authors;
        formData = FormData()
        formData.title.value = book.title
        formData.subtitle.value = book.subtitle ?: ""
        formData.description.value = book.description ?: ""
        formData.publisher.value = book.publisher ?: ""
        formData.published.value = book.published
        formData.isOwned.value = book.isOwned
        formData.media.value = book.media
        formData.progress.value = book.progress
        formData.coverUri.value = book.coverURI
        formData.identifierType.value = book.industryIdentifierType
        formData.identifier.value = book.identifier ?: ""
        formData.language.value = book.language
        formData.authors.addAll(authors)
        formData.note.value = if(bookBundle.note != null) bookBundle.note.note else ""
    }

    fun submitBook() {
        viewModelScope.launch(Dispatchers.IO) {
            db.insertBookBundle(convertFormDataToBookBundle())
        }
    }

    private fun convertFormDataToBookBundle(): BookBundle{
        val book = Book(
            bookId = currentId,
            title = formData.title.value,
            subtitle = formData.subtitle.value.nullIfEmptyOrBlank(),
            description = formData.description.value.nullIfEmptyOrBlank(),
            publisher = formData.publisher.value.nullIfEmptyOrBlank(),
            published = formData.published.value,
            isOwned = formData.isOwned.value,
            progress = formData.progress.value,
            coverURI = formData.coverUri.value,
            industryIdentifierType = formData.identifierType.value,
            identifier = formData.identifier.value.nullIfEmptyOrBlank(),
            media = formData.media.value,
            //TODO: implement score
            score = null,
            language = formData.language.value
        )
        val note = if (formData.note.value.isBlank()) null else Note(currentId, formData.note.value)
        //TODO: add missing fields to form
        return BookBundle(book, formData.authors, listOf<Genre>(), null, null, null, null, null, note, null)
    }

}
