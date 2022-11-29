package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.BookMetaRepository
import com.guidofe.pocketlibrary.repositories.DataStoreRepository
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialogState
import com.guidofe.pocketlibrary.ui.utils.translateGenresWithState
import com.guidofe.pocketlibrary.utils.BookDestination
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

    private suspend fun manageTranslation(callback: () -> Unit) {
        settingsLiveData.value?.let { settings ->
            if (settings.allowGenreTranslation && settings.language.code != "en") {
                translateGenresWithState(
                    code = settings.language.code,
                    state = translationDialogState,
                    coroutineScope = viewModelScope,
                    repo = localRepo,
                ) {
                    // TODO: Manage result
                    callback()
                }
            } else {
                callback()
            }
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
                val id = it.saveToDestination(destination, localRepo)
                ids.add(id)
            }
            manageTranslation { callback(ids) }
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
            val id = importedBook.saveToDbAsBookBundle(localRepo)
            manageTranslation { callback(id) }
        }
    }

    override fun saveImportedBooksAsBookBundles(
        importedBooks: List<ImportedBookData>,
        callback: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            importedBooks.forEach {
                it.saveToDbAsBookBundle(localRepo)
            }
            manageTranslation { callback() }
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
                        viewModelScope.launch(Dispatchers.IO) {
                            manageTranslation { onOneBookSaved() }
                        }
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
}