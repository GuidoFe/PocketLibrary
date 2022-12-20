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
import com.guidofe.pocketlibrary.utils.TranslationPhase
import com.guidofe.pocketlibrary.utils.TranslationService
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImportedBookVM @Inject constructor(
    private val localRepo: LocalRepository,
    private val metaRepo: BookMetaRepository,
    override val snackbarHostState: SnackbarHostState,
    dataStore: DataStoreRepository
) : ViewModel(), IImportedBookVM {
    override val settingsLiveData = dataStore.settingsLiveData
    override fun getImportedBooksFromIsbn(
        isbn: String,
        failureCallback: (message: String) -> Unit,
        maxResults: Int,
        callback: (books: List<ImportedBookData>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = metaRepo.fetchVolumesByIsbn(isbn, maxResults)
            withContext(Dispatchers.Main) {
                if (res.isSuccess())
                    callback(res.data ?: emptyList())
                else
                    failureCallback(res.message ?: "Error")
            }
        }
    }

    override fun getBooksInLibraryWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInLibraryWithSameIsbn(isbn)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    override fun saveImportedBooks(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?,
        callback: (List<Long>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = saveImportedBooksToDestination(
                importedBooks, destination, translationDialogState
            )
            withContext(Dispatchers.Main) {
                callback(ids)
            }
        }
    }

    override fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = saveImportedBookToDestination(
                importedBook, destination, translationDialogState
            )
            withContext(Dispatchers.Main) {
                callback(id)
            }
        }
    }

    override fun getBooksInWishlistWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInWishlistWithSameIsbn(isbn)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    override fun getBooksInBorrowedWithSameIsbn(isbn: String, callback: (List<Book>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = localRepo.getBooksInBorrowedWithSameIsbn(isbn)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    override fun getAndSaveBookFromIsbnFlow(
        isbn: String,
        destination: BookDestination,
        onNetworkError: () -> Unit,
        onNoBookFound: () -> Unit,
        onOneBookSaved: () -> Unit,
        onMultipleBooksFound: (List<ImportedBookData>) -> Unit,
        translationDialogState: TranslationDialogState?
    ) {

        getImportedBooksFromIsbn(
            isbn,
            failureCallback = {
                onNetworkError()
            },
            maxResults = 20
        ) { importedList ->
            when (importedList.size) {
                0 -> {
                    onNoBookFound()
                }
                1 -> {
                    saveImportedBook(importedList[0], destination, translationDialogState) {
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

    private suspend fun insertImportedAuthors(authors: List<String>): Map<String, Long> {
        val distinctAuthors = authors.distinct()
        val existingAuthors = localRepo.getExistingAuthors(distinctAuthors)
        val existingAuthorsNames = existingAuthors.map { it.name }
        val authorsMap = existingAuthors.associate { Pair(it.name, it.authorId) }.toMutableMap()
        val authorsToInsert = distinctAuthors.filter { !existingAuthorsNames.contains(it) }
        val newAuthorsIds = localRepo.insertAllAuthors(authorsToInsert.map { Author(0L, it) })
        authorsMap.putAll(authorsToInsert.zip(newAuthorsIds).toMap())
        return authorsMap
    }

    private suspend fun insertImportedGenres(
        genres: List<String>,
        translationDialogState: TranslationDialogState?
    ): Map<String, Long>? {
        val distinctGenres = genres.distinct()
        val l = settingsLiveData.value?.language?.code
        val targetLanguage = if (l == null) {
            Log.e("debug", "Language code is null")
            "en"
        } else l
        val translateGenres = settingsLiveData.value?.allowGenreTranslation ?: false
        if (targetLanguage == "en" || !translateGenres) {
            localRepo.insertAllGenres(distinctGenres.map { Genre(0L, it, it, "en") })
            return localRepo.getGenresByNames(distinctGenres).associate {
                Pair(it.name, it.genreId)
            }
        }
        translationDialogState?.translationPhase = TranslationPhase.FETCHING_GENRES
        val existingGenres = localRepo.getGenresByEnglishNames(distinctGenres)
        val existingGenresNames = existingGenres.map { it.englishName!! }
        val genresToTranslate = distinctGenres.filter { !existingGenresNames.contains(it) }
        if (genresToTranslate.isNotEmpty()) {
            translationDialogState?.totalGenres = genresToTranslate.size
            translationDialogState?.translationPhase = TranslationPhase.DOWNLOADING_TRANSLATOR
            val translator = TranslationService(targetLanguage)
            val res = translator.initTranslator()
            if (!res) {
                Log.e("debug", "Error initializing translator")
                // TODO: Manage error
                translationDialogState?.translationPhase = TranslationPhase.NO_TRANSLATING
                return null
            }

            translationDialogState?.translationPhase = TranslationPhase.TRANSLATING
            val translatedGenres = genresToTranslate.map {
                val translation = translator.translate(it)
                translationDialogState?.genresTranslated?.let { n ->
                    translationDialogState.genresTranslated = n + 1
                }
                if (translation == null) {
                    Log.e("debug", "Impossible to translate $it")
                    Genre(0L, it, it, "en")
                } else {
                    Genre(0L, translation, it, targetLanguage)
                }
            }
            translator.close()
            translationDialogState?.translationPhase = TranslationPhase.UPDATING_DB
            localRepo.insertAllGenres(translatedGenres)
        }
        translationDialogState?.translationPhase = TranslationPhase.NO_TRANSLATING
        return localRepo.getGenresByEnglishNames(distinctGenres).associate {
            Pair(it.englishName!!, it.genreId)
        }
    }

    private suspend fun saveImportedBookAsBookBundle(
        importedBook: ImportedBookData,
        translationDialogState: TranslationDialogState?
    ): Long {
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
            val genreMap = insertImportedGenres(importedBook.genres, translationDialogState)
            // TODO: Manage failure
            if (genreMap != null)
                localRepo.insertAllBookGenres(genreMap.values.map { BookGenre(bookId, it) })
            else
                Log.e("debug", "Inserting imported genres failed")
        }
        return bookId
    }

    private suspend fun saveImportedBooksAsBookBundles(
        importedBooks: List<ImportedBookData>,
        translationDialogState: TranslationDialogState?
    ): List<Long> {
        val books = importedBooks.map {
            Book(
                bookId = 0L,
                title = it.title,
                subtitle = it.subtitle,
                description = it.description,
                publisher = it.publisher,
                published = it.published,
                coverURI = it.coverUrl?.let { s -> Uri.parse(s) },
                identifier = it.identifier,
                isEbook = it.isEbook,
                language = it.language,
                pageCount = it.pageCount
            )
        }
        val bookIds = localRepo.insertAllBooks(books)
        val importedBooksMap = bookIds.zip(importedBooks).toMap().filter {
            if (it.key < 0) {
                Log.e("debug", "Couldn't insert imported book ${it.value.title}")
                false
            } else true
        }
        val authorsMap = insertImportedAuthors(importedBooks.flatMap { it.authors })
        val bookAuthorsToInsert = importedBooksMap.flatMap {
            it.value.authors.mapIndexed { i, a -> BookAuthor(it.key, authorsMap[a]!!, i) }
        }
        localRepo.insertAllBookAuthors(bookAuthorsToInsert)
        val genreMap = insertImportedGenres(
            importedBooks.flatMap { it.genres }, translationDialogState
        )
        // TODO: Manage failure
        if (genreMap != null) {
            val bookGenresToInsert = importedBooksMap.flatMap { entry ->
                entry.value.genres.mapNotNull { g ->
                    if (genreMap[g] == null) {
                        Log.e("debug", "Genre $g of book ${entry.key} not in db")
                        null
                    } else BookGenre(entry.key, genreMap[g]!!)
                }
            }
            localRepo.insertAllBookGenres(bookGenresToInsert)
        } else
            Log.e("clock", "Inserting imported genres failed")
        return bookIds
    }

    private suspend fun saveImportedBookToDestination(
        importedBook: ImportedBookData,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?
    ): Long {
        val id = saveImportedBookAsBookBundle(importedBook, translationDialogState)
        when (destination) {
            BookDestination.BORROWED -> localRepo.insertBorrowedBook(
                BorrowedBook(id, notificationTime = null)
            )
            BookDestination.WISHLIST -> localRepo.insertWishlistBook(WishlistBook(id))
            BookDestination.LIBRARY -> localRepo.insertLibraryBook(LibraryBook(id))
        }
        return id
    }

    private suspend fun saveImportedBooksToDestination(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination,
        translationDialogState: TranslationDialogState?
    ): List<Long> {
        val ids = saveImportedBooksAsBookBundles(importedBooks, translationDialogState)
        when (destination) {
            BookDestination.BORROWED -> {
                localRepo.insertBorrowedBooks(
                    ids.map { BorrowedBook(it, notificationTime = null) }
                )
            }
            BookDestination.WISHLIST -> localRepo.insertWishlistBooks(
                ids.map { WishlistBook(it) }
            )
            BookDestination.LIBRARY -> localRepo.insertLibraryBooks(
                ids.map { LibraryBook(it) }
            )
        }
        return ids
    }
}