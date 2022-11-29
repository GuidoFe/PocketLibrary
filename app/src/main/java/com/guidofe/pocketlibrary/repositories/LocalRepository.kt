package com.guidofe.pocketlibrary.repositories

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.WishlistBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.utils.TranslationPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun <R : Any?> withTransaction(block: suspend () -> R): R
    fun close()
    suspend fun insertBookBundle(bundle: BookBundle): Long
    suspend fun getBookBundle(bookId: Long): BookBundle?

    suspend fun getLibraryBundles(pageSize: Int, pageNumber: Int): List<LibraryBundle>
    suspend fun getLibraryBundlesWithSameIsbns(isbnList: List<String>): List<LibraryBundle>

    suspend fun insertAllAuthors(authors: List<Author>): List<Long>
    suspend fun getExistingAuthors(authorsNames: List<String>): List<Author>

    suspend fun insertBook(book: Book): Long
    suspend fun updateBook(book: Book)

    suspend fun insertAllBookAuthors(bookAuthors: List<BookAuthor>)

    suspend fun upsertNote(note: Note)
    suspend fun deleteNote(note: Note)

    suspend fun insertAllGenres(genres: List<Genre>): List<Long>
    suspend fun getGenresByNames(names: List<String>): List<Genre>

    suspend fun insertAllBookGenres(bookGenres: List<BookGenre>)

    suspend fun deleteBook(book: Book)
    suspend fun deleteBooks(books: List<Book>)

    suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean)
    suspend fun updateFavorite(bookId: Long, isFavorite: Boolean)
    suspend fun deleteBooksByIds(ids: List<Long>)
    suspend fun getBookBundlesWithSameIsbn(isbn: String): List<BookBundle>
    suspend fun insertLibraryBook(libraryBook: LibraryBook)
    suspend fun getBooksInLibraryWithSameIsbn(isbn: String): List<Book>
    suspend fun getBooksInLibraryWithSameIsbns(isbns: List<String>): List<Book>
    suspend fun getWishlistBundles(pageSize: Int, pageNumber: Int): List<WishlistBundle>
    suspend fun insertWishlistBook(wishlistBook: WishlistBook)
    suspend fun getBooksInWishlistWithSameIsbn(isbn: String): List<Book>
    suspend fun moveBookFromWishlistToLibrary(bookId: Long)
    suspend fun moveBooksFromWishlistToLibrary(bookIds: List<Long>)

    fun getBorrowedBundles(isReturned: Boolean): Flow<List<BorrowedBundle>>
    fun getBorrowedBundles(): Flow<List<BorrowedBundle>>
    suspend fun insertBorrowedBook(borrowedBook: BorrowedBook)
    suspend fun getBooksInBorrowedWithSameIsbn(isbn: String): List<Book>
    suspend fun updateBorrowedBook(borrowedBook: BorrowedBook)
    suspend fun insertLentBook(lentBook: LentBook)
    suspend fun insertAllLentBooks(lentBooks: List<LentBook>)
    suspend fun updateLentBook(lentBook: LentBook)
    suspend fun deleteLentBook(lentBook: LentBook)
    suspend fun deleteLentBooks(lentBooks: List<LentBook>)
    suspend fun updateAllBorrowedBooks(borrowedBooks: List<BorrowedBook>)
    fun getLentLibraryBundles(): Flow<List<LibraryBundle>>
    suspend fun updateAllLentBooks(lentBooks: List<LentBook>)
    suspend fun upsertProgress(progress: Progress)
    suspend fun deleteProgress(bookId: Long)
    suspend fun updatePageNumber(bookId: Long, value: Int)
    suspend fun deleteBookAuthors(bookId: Long)
    suspend fun getGenresByStartingLetters(start: String): List<Genre>
    suspend fun deleteBookGenreRelations(bookId: Long)
    suspend fun setBorrowedBookStatus(bookIds: List<Long>, isReturned: Boolean)
    suspend fun deleteBorrowedBooks(ids: List<Long>)
    suspend fun insertLibraryBooks(libraryBooks: List<LibraryBook>)
    suspend fun getBorrowedBundles(
        pageSize: Int,
        pageNumber: Int,
        withReturned: Boolean
    ): List<BorrowedBundle>

    suspend fun getStats(): AppStats
    suspend fun getBookBundlesAtProgressPhase(phase: ProgressPhase): List<BookBundle>
    suspend fun countLibraryBooksAtEveryPhase(): Map<ProgressPhase, Int>

    suspend fun getLibraryBundlesWithCustomFilter(
        pageSize: Int,
        pageNumber: Int,
        query: LibraryQuery
    ): List<LibraryBundle>

    suspend fun getAllGenres(): List<Genre>
    suspend fun updateAllGenres(genres: List<Genre>)
    suspend fun getGenresOfDifferentLanguage(languageCode: String): List<Genre>
    suspend fun translateGenres(
        targetLanguageCode: String,
        coroutineScope: CoroutineScope,
        onPhaseChanged: (TranslationPhase) -> Unit = {},
        onCountedTotalGenresToUpdate: (Int) -> Unit = {},
        onTranslatedGenresCountUpdate: (Int) -> Unit = {},
        onFinish: (success: Boolean) -> Unit = {}
    )
}