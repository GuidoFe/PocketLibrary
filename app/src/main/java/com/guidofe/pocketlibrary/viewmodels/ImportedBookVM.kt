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
        viewModelScope.launch(Dispatchers.IO) {
            val ids = saveImportedBooksToDestination(importedBooks, destination)
            callback(ids)
        }
    }

    override fun saveImportedBook(
        importedBook: ImportedBookData,
        destination: BookDestination,
        callback: (Long) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = saveImportedBookToDestination(importedBook, destination)
            callback(id)
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
                    saveImportedBook(importedList[0], destination) {
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

    private suspend fun insertImportedGenres(genres: List<String>): Map<String, Long>? {
        val distinctGenres = genres.distinct()
        val existingEnglishGenres = localRepo.getGenresByEnglishNames(distinctGenres)
        val map = existingEnglishGenres.associate {
            Pair(it.englishName!!, it.genreId)
        }.toMutableMap()
        val genresToInsert = mutableListOf<Genre>()
        val existingEnglishGenresNames = existingEnglishGenres.map { it.englishName!!.lowercase() }
        val genresToTranslate = distinctGenres.filter {
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
            map.putAll(existingTranslatedGenres.associate { Pair(it.englishName!!, it.genreId) })
            val existingTranslatedGenresNames = existingTranslatedGenres.map { it.name.lowercase() }
            genresToInsert.addAll(
                translatedGenres.filter {
                    !existingTranslatedGenresNames.contains(it.name.lowercase())
                }
            )
            val newIds = localRepo.insertAllGenres(genresToInsert)
            map.putAll(genresToInsert.map { it.englishName!! }.zip(newIds))
        }
        return map
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
            val genreMap = insertImportedGenres(importedBook.genres)
            // TODO: Manage failure
            if (genreMap != null)
                localRepo.insertAllBookGenres(genreMap.values.map { BookGenre(bookId, it) })
            else
                Log.e("debug", "Inserting imported genres failed")
        }
        return bookId
    }

    private suspend fun saveImportedBooksAsBookBundles(
        importedBooks: List<ImportedBookData>
    ): List<Long> {
        val books = importedBooks.map {
            Book(
                bookId = 0L,
                title = it.title,
                subtitle = it.subtitle,
                description = it.description,
                publisher = it.publisher,
                published = it.published,
                coverURI = it.coverUrl?.let { Uri.parse(it) },
                identifier = it.identifier,
                isEbook = it.isEbook,
                language = it.language,
                pageCount = it.pageCount
            )
        }
        val bookIds = localRepo.insertAllBooks(books)
        val importedBooksMap = bookIds.zip(importedBooks).toMap()
        val authorsMap = insertImportedAuthors(importedBooks.flatMap { it.authors })
        val bookAuthorsToInsert = importedBooksMap.flatMap {
            it.value.authors.mapIndexed { i, a -> BookAuthor(it.key, authorsMap[a]!!, i) }
        }
        localRepo.insertAllBookAuthors(bookAuthorsToInsert)
        val importedGenres = importedBooks.flatMap { it.genres }
        val genreMap = insertImportedGenres(importedBooks.flatMap { it.genres })
        // TODO: Manage failure
        if (genreMap != null) {
            val bookGenresToInsert = importedBooksMap.flatMap {
                it.value.genres.map { g -> BookGenre(it.key, genreMap[g]!!) }
            }
            localRepo.insertAllBookGenres(bookGenresToInsert)
        } else
            Log.e("debug", "Inserting imported genres failed")
        return bookIds
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

    private suspend fun saveImportedBooksToDestination(
        importedBooks: List<ImportedBookData>,
        destination: BookDestination
    ): List<Long> {
        val ids = saveImportedBooksAsBookBundles(importedBooks)
        when (destination) {
            BookDestination.BORROWED -> localRepo.insertBorrowedBooks(
                ids.map { BorrowedBook(it) }
            )
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