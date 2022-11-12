package com.guidofe.pocketlibrary.repositories

import androidx.room.withTransaction
import com.guidofe.pocketlibrary.data.local.library_db.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultLocalRepository @Inject constructor(val db: AppDatabase) : LocalRepository {
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

    override fun getBorrowedBundles(): Flow<List<BorrowedBundle>> {
        return db.borrowedBundleDao().getBorrowedBundles()
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
        return db.genreDao().getGenresByNames(names)
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
}