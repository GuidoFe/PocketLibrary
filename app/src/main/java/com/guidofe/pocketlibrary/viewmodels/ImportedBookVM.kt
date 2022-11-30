package com.guidofe.pocketlibrary.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.utils.TranslationService
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val localRepo: LocalRepository,
    private val metaRepo: BookMetaRepository,
    override val snackbarHostState: SnackbarHostState,
    dataStore: DataStoreRepository
) : ViewModel(), IImportedBookVM {
    override val translationDialogState = TranslationDialogState()
    override val settingsLiveData = dataStore.settingsLiveData
    override fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = metaRepo.fetchVolumesByIsbn(isbn, maxResults)
            if (res.isSuccess())
                callback(res.data ?: emptyList())
            else
                failureCallback(res.message ?: "Error")
        }
    }

    override fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInLibraryWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        callback: (List<Long>) -> Unit
    ) {
        val ids = mutableListOf<Long>()
        viewModelScope.launch(Dispatchers.IO) {
            importedBooks.forEach {
                val id = saveImportedBookToDestination(it, destination)
                ids.add(id)
            }
            callback(ids)
        }
    }

    override fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInWishlistWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInBorrowedWithSameIsbn(isbn)
            callback(list)
        }
    }

    override fun saveImportedBookAsBookBundle(
        importedBook: ImportedBookData,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = saveImportedBookAsBookBundle(importedBook)
            callback(id)
        }
    }

    override fun saveImportedBooksAsBookBundles(
        importedBooks: List<ImportedBookData>,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            importedBooks.forEach {
                saveImportedBookAsBookBundle(it)
            }
            callback()
        }
    }

    override fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit,
    ) {

        getImportedBooksFromIsbn(
            isbn,
            failureCallback = {
                onNetworkError()
            }
        ) { importedList ->
            when (importedList.size) {
                0 -> {
                    onNoBookFound()
                }
                1 -> {
                    saveImportedBooks(listOf(importedList[0]), destination) {
                        onOneBookSaved()
                    }
                }
                else -> {
                    onMultipleBooksFound(importedList)
                }
            }
        }
    }

    override fun checkIfImportedBooksAreAlreadyInLibrary(
        list: List<ImportedBookData>,
        onAllOk: () -> Unit,
        onConflict: (
            booksOk: List<ImportedBookData>,
            duplicateBooks: List<ImportedBookData>
        ) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val conflictBooks = localRepo.getLibraryBundlesWithSameIsbns(
                list.mapNotNull { it.identifier }
            )
            val conflictIsbn = conflictBooks.map { it.bookBundle.book.identifier!! }
            if (conflictIsbn.isEmpty()) {
                onAllOk()
                return@launch
            }
            val booksOk = mutableListOf<ImportedBookData>()
            val duplicateBooks = mutableListOf<ImportedBookData>()
            list.forEach {
                if (conflictIsbn.contains(it.identifier))
                    duplicateBooks.add(it)
                else
                    booksOk.add(it)
            }
            onConflict(booksOk, duplicateBooks)
        }
    }

    private suspend fun insertImportedGenres(genres: List<String>): List<Long>? {
        val existingEnglishGenres = localRepo.getGenresByEnglishNames(genres)
        val genresIds = existingEnglishGenres.map { it.genreId }.toMutableList()
        val genresToInsert = mutableListOf<Genre>()
        val existingEnglishGenresNames = existingEnglishGenres.map { it.englishName!!.lowercase() }
        val genresToTranslate = genres.filter {
            !existingEnglishGenresNames.contains(it.lowercase())
        }
        if (genresToTranslate.isNotEmpty()) {
            val targetLanguage = settingsLiveData.value?.language?.code ?: "en"
            val translator = TranslationService(targetLanguage)
            val res = translator.initTranslator()
            if (!res) {
                Log.e("debug", "Error initializing translator")
                // TODO: Manage error
                return null
            }
            val translatedGenres = genresToTranslate.mapNotNull {
                val translation = translator.translate(it)
                if (translation == null) {
                    genresToInsert.add(Genre(0L, it, it, "en"))
                    null
                } else {
                    Genre(0L, translation, it, targetLanguage)
                }
            }
            translator.close()
            val translatedGenresNames = translatedGenres.map { it.name }
            val existingTranslatedGenres = localRepo.getGenresByNames(translatedGenresNames)
            val existingTranslatedGenresNames = existingTranslatedGenres.map { it.name.lowercase() }
            genresIds.addAll(existingTranslatedGenres.map { it.genreId })
            genresToInsert.addAll(
                translatedGenres.filter {
                    !existingTranslatedGenresNames.contains(it.name.lowercase())
                }
            )
            val newIds = localRepo.insertAllGenres(genresToInsert)
            genresIds.addAll(newIds)
        }
        return genresIds
    }

    private suspend fun saveImportedBookAsBookBundle(importedBook: ImportedBookData): Long {
        val book = Book(
            bookId = 0L,
            title = importedBook.title,
            subtitle = importedBook.subtitle,
            description = importedBook.description,
            publisher = importedBook.publisher,
            published = importedBook.published,
            coverURI = importedBook.coverUrl?.let { Uri.parse(it) },
            identifier = importedBook.identifier,
            isEbook = importedBook.isEbook,
            language = importedBook.language,
            pageCount = importedBook.pageCount
        )
        val bookId = localRepo.insertBook(book)
        localRepo.insertAllAuthors(
            importedBook.authors.map { name ->
                Author(0L, name)
            }
        )
        val authorsList = localRepo.getExistingAuthors(importedBook.authors)
        localRepo.insertAllBookAuthors(
            authorsList.map {
                BookAuthor(bookId, it.authorId, importedBook.authors.indexOf(it.name))
            }
        )
        if (importedBook.genres.isNotEmpty()) {
            val ids = insertImportedGenres(importedBook.genres)
            // TODO: Manage failure
            if (ids != null)
                localRepo.insertAllBookGenres(ids.map { BookGenre(bookId, it) })
            else
                Log.e("debug", "Inserting imported genres failed")
        }
        return bookId
    }

    private suspend fun saveImportedBookToDestination(
        importedBook: ImportedBookData,
        destination: BookDestination
    ): Long {
        val id = saveImportedBookAsBookBundle(importedBook)
        when (destination) {
            BookDestination.BORROWED -> localRepo.insertBorrowedBook(BorrowedBook(id))
            BookDestination.WISHLIST -> localRepo.insertWishlistBook(WishlistBook(id))
            BookDestination.LIBRARY -> localRepo.insertLibraryBook(LibraryBook(id))
        }
        return id
    }
}