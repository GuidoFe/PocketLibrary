package com.guidofe.pocketlibrary.repositories

import android.util.Log
import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.guidofe.pocketlibrary.data.local.library_db.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.utils.TranslationPhase
import com.guidofe.pocketlibrary.utils.TranslationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    private val db: AppDatabase,
) : LocalRepository {
    override suspend fun insertBookBundle(bundle: BookBundle): Long {
        var bookId: Long = -1L
        withTransaction {
            bookId = insertBook(bundle.book)
            val authorsToInsert: ArrayList<Author> = arrayListOf()
            val authorsId = arrayListOf<Long>()
            bundle.authors.forEach { author ->
                if (author.authorId == 0L) {
                    authorsToInsert.add(author)
                    authorsId.add(-1)
                } else
                    authorsId.add(author.authorId)
            }
            if (authorsToInsert.isNotEmpty()) {
                var i = 0
                val newIds = insertAllAuthors(authorsToInsert)
                authorsId.forEachIndexed { index, value ->
                    if (value < 0) {
                        authorsId[index] = newIds[i]
                        i++
                    }
                }
            }
            insertAllBookAuthors(
                authorsId.mapIndexed {
                    index, id ->
                    BookAuthor(bookId, id, index)
                }
            )
            val genresToInsert: ArrayList<Genre> = arrayListOf()
            val genresId = arrayListOf<Long>()
            bundle.genres.forEach { genre ->
                if (genre.genreId == 0L)
                    genresToInsert.add(genre)
                else
                    genresId.add(genre.genreId)
            }
            if (genresToInsert.isNotEmpty()) {
                val newIds = insertAllGenres(genresToInsert)
                genresId.addAll(newIds)
            }
            insertAllBookGenres(genresId.map { id -> BookGenre(bookId, id) })
            var placeId: Long? = null
            var roomId: Long? = null
            var bookshelfId: Long? = null
            if (bundle.note != null)
                upsertNote(bundle.note.copy(bookId = bookId))
        }
        return bookId
    }

    override suspend fun insertLibraryBook(libraryBook: LibraryBook) {
        db.libraryBookDao().insert(libraryBook)
    }

    override suspend fun insertLibraryBooks(libraryBooks: List<LibraryBook>) {
        db.libraryBookDao().insertAll(libraryBooks)
    }

    override suspend fun insertWishlistBook(wishlistBook: WishlistBook) {
        db.wishlistBookDao().insert(wishlistBook)
    }

    override suspend fun insertBorrowedBook(borrowedBook: BorrowedBook) {
        db.borrowedBookDao().insert(borrowedBook)
    }

    override suspend fun deleteBooks(books: List<Book>) {
        db.bookDao().delete(books)
    }

    override suspend fun deleteBook(book: Book) {
        db.bookDao().delete(book)
    }

    override suspend fun deleteBooksByIds(ids: List<Long>) {
        db.bookDao().deleteByIds(ids)
    }

    override suspend fun getBookBundle(bookId: Long): BookBundle {
        return db.bookBundleDao().getBookBundle(bookId)
    }

    override suspend fun getLibraryBundles(pageSize: Int, pageNumber: Int): List<LibraryBundle> {
        return db.libraryBundleDao().getLibraryBundles(pageNumber * pageSize, pageSize)
    }

    override suspend fun getBorrowedBundles(
        pageSize: Int,
        pageNumber: Int,
        withReturned: Boolean
    ): List<BorrowedBundle> {
        val offset = pageNumber * pageSize
        return if (withReturned)
            db.borrowedBundleDao().getBorrowedBundlesWithReturned(offset, pageSize)
        else
            db.borrowedBundleDao().getBorrowedBundlesWithoutReturned(offset, pageSize)
    }

    override suspend fun getWishlistBundles(pageSize: Int, pageNumber: Int): List<WishlistBundle> {
        return db.wishlistBundleDao().getWishlistBundles(pageNumber * pageSize, pageSize)
    }
    override suspend fun getLibraryBundlesWithSameIsbns(
        isbnList: List<String>
    ): List<LibraryBundle> {
        return db.libraryBundleDao().getLibraryBundlesWithSameIsbns(isbnList)
    }

    override suspend fun getBooksInLibraryWithSameIsbn(isbn: String): List<Book> {
        return db.libraryBookDao().getBooksInLibraryWithSameIsbn(isbn)
    }

    override suspend fun getBooksInWishlistWithSameIsbn(isbn: String): List<Book> {
        return db.wishlistBookDao().getBooksInWishlistWithSameIsbn(isbn)
    }

    override suspend fun getBooksInBorrowedWithSameIsbn(isbn: String): List<Book> {
        return db.borrowedBookDao().getBooksInBorrowedWithSameIsbn(isbn)
    }
    override suspend fun moveBookFromWishlistToLibrary(bookId: Long) {
        db.wishlistBookDao().delete(bookId)
        db.libraryBookDao().insert(LibraryBook(bookId))
    }

    override suspend fun moveBooksFromWishlistToLibrary(bookIds: List<Long>) {
        db.wishlistBookDao().deleteAll(bookIds)
        db.libraryBookDao().insertAll(bookIds.map { LibraryBook(it) })
    }

    override fun getBorrowedBundles(isReturned: Boolean): Flow<List<BorrowedBundle>> {
        return db.borrowedBundleDao().getBorrowedBundles(isReturned)
    }

    override fun getBorrowedBundles(): Flow<List<BorrowedBundle>> {
        return db.borrowedBundleDao().getBorrowedBundles()
    }

    override suspend fun deleteBorrowedBooks(ids: List<Long>) {
        db.borrowedBookDao().delete(ids)
    }

    override fun getLentLibraryBundles(): Flow<List<LibraryBundle>> {
        return db.libraryBundleDao().getLentLibraryBundlesFlow()
    }

    override suspend fun getBooksInLibraryWithSameIsbns(isbns: List<String>): List<Book> {
        return db.libraryBookDao().getBooksInLibraryWithSameIsbns(isbns)
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return db.withTransaction(block)
    }

    override suspend fun insertBook(book: Book): Long {
        return db.bookDao().insert(book)
    }

    override suspend fun updateBook(book: Book) {
        db.bookDao().update(book)
    }

    override suspend fun insertAllAuthors(authors: List<Author>): List<Long> {
        return db.authorDao().insertAll(authors)
    }

    override suspend fun getExistingAuthors(authorsNames: List<String>): List<Author> {
        return db.authorDao().getExistingAuthors(authorsNames)
    }

    override suspend fun insertAllBookAuthors(bookAuthors: List<BookAuthor>) {
        db.bookAuthorDao().insertAll(bookAuthors)
    }

    override suspend fun deleteBookAuthors(bookId: Long) {
        db.bookAuthorDao().delete(bookId)
    }

    override suspend fun upsertNote(note: Note) {
        db.noteDao().upsert(note)
    }

    override suspend fun deleteNote(note: Note) {
        db.noteDao().delete(note)
    }

    override suspend fun insertAllGenres(genres: List<Genre>): List<Long> {
        return db.genreDao().insertAll(genres)
    }

    override suspend fun getGenresByNames(names: List<String>): List<Genre> {
        return db.genreDao().getGenresByNames(names.map { it.lowercase() })
    }

    override suspend fun getGenresByStartingLetters(start: String): List<Genre> {
        return db.genreDao().getFromStartingLetters(start)
    }

    override suspend fun deleteBookGenreRelations(bookId: Long) {
        return db.bookGenreDao().delete(bookId)
    }
    override suspend fun insertAllBookGenres(bookGenres: List<BookGenre>) {
        db.bookGenreDao().insertAll(bookGenres)
    }

    override fun close() {
        db.close()
    }

    override suspend fun getBookBundlesWithSameIsbn(isbn: String): List<BookBundle> {
        return db.bookBundleDao().getBookBundlesWithSameIsbn(isbn)
    }

    override suspend fun updateFavorite(bookId: Long, isFavorite: Boolean) {
        db.libraryBookDao().updateFavorite(bookId, isFavorite)
    }

    override suspend fun updateFavorite(bookIds: List<Long>, isFavorite: Boolean) {
        db.libraryBookDao().updateFavorite(bookIds, isFavorite)
    }

    override suspend fun updateBorrowedBook(borrowedBook: BorrowedBook) {
        db.borrowedBookDao().update(borrowedBook)
    }

    override suspend fun updateAllBorrowedBooks(borrowedBooks: List<BorrowedBook>) {
        db.borrowedBookDao().updateAll(borrowedBooks)
    }

    override suspend fun insertLentBook(lentBook: LentBook) {
        db.lentBookDao().insert(lentBook)
    }

    override suspend fun insertAllLentBooks(lentBooks: List<LentBook>) {
        db.lentBookDao().insertAll(lentBooks)
    }

    override suspend fun updateLentBook(lentBook: LentBook) {
        db.lentBookDao().update(lentBook)
    }

    override suspend fun updateAllLentBooks(lentBooks: List<LentBook>) {
        db.lentBookDao().updateAll(lentBooks)
    }

    override suspend fun deleteLentBook(lentBook: LentBook) {
        db.lentBookDao().delete(lentBook)
    }

    override suspend fun deleteLentBooks(lentBooks: List<LentBook>) {
        db.lentBookDao().delete(lentBooks)
    }

    override suspend fun upsertProgress(progress: Progress) {
        db.progressDao().upsert(progress)
    }

    override suspend fun deleteProgress(bookId: Long) {
        db.progressDao().delete(bookId)
    }

    override suspend fun updatePageNumber(bookId: Long, value: Int) {
        db.bookDao().changePageNumber(bookId, value)
    }

    override suspend fun setBorrowedBookStatus(bookIds: List<Long>, isReturned: Boolean) {
        db.borrowedBookDao().setReturnStatus(bookIds, isReturned)
    }

    override suspend fun countLibraryBooksAtEveryPhase(): Map<ProgressPhase, Int> {
        return db.libraryBookDao().countLibraryBooksAtEveryPhase()
    }

    // TODO: Optimize queries
    override suspend fun getStats(): AppStats {
        val phaseCount = countLibraryBooksAtEveryPhase()
        return AppStats(
            libraryBooksCount = db.libraryBookDao().countBooksInLibrary(),
            currentlyBorrowedBooksCount = db.borrowedBookDao().countCurrentlyBorrowedBooks(),
            lentBooksCount = db.lentBookDao().countLentBooks(),
            totalReadBooksCount = db.progressDao().countBooks(ProgressPhase.READ),
            booksCurrentlyReading = db.bookBundleDao()
                .getBookBundlesAtProgressPhase(ProgressPhase.IN_PROGRESS),
            libraryBooksCurrentlyReading = phaseCount[ProgressPhase.IN_PROGRESS] ?: 0,
            libraryBooksDnf = phaseCount[ProgressPhase.DNF] ?: 0,
            libraryBooksRead = phaseCount[ProgressPhase.READ] ?: 0,
            libraryBooksSuspended = phaseCount[ProgressPhase.SUSPENDED] ?: 0
        )
    }

    override suspend fun getBookBundlesAtProgressPhase(phase: ProgressPhase): List<BookBundle> {
        return db.bookBundleDao().getBookBundlesAtProgressPhase(phase)
    }

    override suspend fun getAllGenres(): List<Genre> {
        return db.genreDao().getAll()
    }

    override suspend fun getGenresOfDifferentLanguage(languageCode: String): List<Genre> {
        return db.genreDao().getGenresOfDifferentLanguage(languageCode)
    }

    override suspend fun updateAllGenres(genres: List<Genre>) {
        db.genreDao().updateAll(genres)
    }

    override suspend fun getGenresByEnglishNames(names: List<String>): List<Genre> {
        return db.genreDao().getGenresByEnglishNames(names.map { it.lowercase() })
    }

    override suspend fun getLibraryBundlesWithCustomFilter(
        pageSize: Int,
        pageNumber: Int,
        query: LibraryQuery,
    ): List<LibraryBundle> {
        var queryString = "SELECT library_book.* FROM library_book NATURAL JOIN book"
        val firstArgumentList = mutableListOf<Any>()
        val lastArgumentList = mutableListOf<Any>()

        val whereList = mutableListOf<String>()
        if (query.title != null) {
            queryString += " NATURAL JOIN (SELECT bookId, INSTR(lower(title), " +
                "lower( ? )) as title_count FROM book WHERE title_count != 0)"
            firstArgumentList.add(query.title)
        } else {
            queryString += " NATURAL JOIN book"
        }
        if (query.onlyFavorite) {
            whereList.add("library_book.isFavorite = 1")
        }
        if (query.mediaFilter != LibraryQuery.MediaFilter.ANY) {
            if (query.mediaFilter == LibraryQuery.MediaFilter.ONLY_BOOKS) {
                whereList.add("book.isEbook = 0")
            } else {
                whereList.add("book.isEbook = 1")
            }
        }
        query.language?.let {
            whereList.add("book.language = ?")
            lastArgumentList.add(it)
        }
        query.author?.let {
            queryString += " NATURAL JOIN (SELECT bookId, INSTR(lower( name ), lower( ? )) as " +
                "author_count FROM BookAuthor NATURAL JOIN Author WHERE author_count != 0 )"
            firstArgumentList.add(it)
        }
        query.genre?.let {
            queryString += " NATURAL JOIN BookGenre NATURAL JOIN Genre"
            whereList.add("Genre.name = ?")
            lastArgumentList.add(it)
        }
        query.progress?.let {
            if (it == ProgressPhase.NOT_READ) {
                queryString += " LEFT JOIN progress ON book.bookId = progress.bookId"
                whereList.add("progress.bookId IS NULL")
            } else {
                queryString += " NATURAL JOIN progress"
                whereList.add("progress.phase = ?")
                lastArgumentList.add(it.name)
            }
        }
        queryString = "$queryString ${
        if (whereList.isEmpty())
            ""
        else
            "WHERE ${whereList.joinToString(" AND ")}"
        }${
        if (query.sortingField != null)
            " ORDER BY ${
            when (query.sortingField) {
                LibraryQuery.LibrarySortField.CREATION -> "library_book.creation"
                LibraryQuery.LibrarySortField.TITLE -> "book.title"
            }
            } ${if (query.reverseOrder) "DESC" else "ASC"}"
        else ""
        } LIMIT ?,?;"
        Log.d("query", queryString)
        val args = arrayOf(
            *firstArgumentList.toTypedArray(),
            *lastArgumentList.toTypedArray(),
            pageNumber * pageSize,
            pageSize
        )
        Log.d("query", args.joinToString(", ") { it.toString() })
        return db.libraryBundleDao().getLibraryBundlesWithCustomQuery(
            SimpleSQLiteQuery(queryString, args)
        )
    }

    override suspend fun translateGenresInDb(
        targetLanguageCode: String,
        coroutineScope: CoroutineScope,
        onPhaseChanged: (TranslationPhase) -> Unit,
        onCountedTotalGenresToUpdate: (Int) -> Unit,
        onTranslatedGenresCountUpdate: (Int) -> Unit,
        onFinish: (success: Boolean) -> Unit
    ) {
        onPhaseChanged(TranslationPhase.FETCHING_GENRES)
        val genresToTranslate = getGenresOfDifferentLanguage(targetLanguageCode)
            .filter { it.englishName != null }
        if (genresToTranslate.isEmpty()) {
            onPhaseChanged(TranslationPhase.NO_TRANSLATING)
            onFinish(true)
            return
        }
        if (targetLanguageCode == "en") {
            onPhaseChanged(TranslationPhase.UPDATING_DB)
            val updatedGenres = genresToTranslate.map {
                it.copy(name = it.englishName!!, lang = "en")
            }
            updateAllGenres(updatedGenres)
            onPhaseChanged(TranslationPhase.NO_TRANSLATING)
            onFinish(true)
            return
        }
        onCountedTotalGenresToUpdate(genresToTranslate.size)
        onPhaseChanged(TranslationPhase.DOWNLOADING_TRANSLATOR)
        val translator = TranslationService(targetLanguageCode)
        val success = translator.initTranslator()
        if (!success) {
            onPhaseChanged(TranslationPhase.NO_TRANSLATING)
            onFinish(false)
            return
        }
        onPhaseChanged(TranslationPhase.TRANSLATING)
        val updatedGenres = genresToTranslate.mapNotNull {
            val translated = translator.translate(it.englishName!!)
            if (translated == null)
                null
            else it.copy(name = translated, lang = targetLanguageCode)
        }
        onPhaseChanged(TranslationPhase.UPDATING_DB)
        updateAllGenres(updatedGenres)
        onFinish(true)
    }
}