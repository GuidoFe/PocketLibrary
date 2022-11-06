package com.guidofe.pocketlibrary.data.local.library_db

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.guidofe.pocketlibrary.data.local.library_db.converters.DateConverter
import com.guidofe.pocketlibrary.data.local.library_db.converters.UriConverter
import com.guidofe.pocketlibrary.data.local.library_db.daos.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.*

@Database(entities = [
    Author::class,
    Book::class,
    LibraryBook::class,
    BookAuthor::class,
    BookGenre::class,
    Genre::class,
    LentBook::class,
    BorrowedBook::class,
    Note::class,
    WishlistBook::class
], version = 9,
    exportSchema = true,
    autoMigrations = [])
@TypeConverters(DateConverter::class, UriConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun bookAuthorDao(): BookAuthorDao
    abstract fun bookBundleDao(): BookBundleDao
    abstract fun borrowedBookDao(): BorrowedBookDao
    abstract fun bookDao(): BookDao
    abstract fun libraryBundleDao(): LibraryBundleDao
    abstract fun borrowedBundleDao(): BorrowedBundleDao
    abstract fun bookGenreDao(): BookGenreDao
    abstract fun genreDao(): GenreDao
    abstract fun noteDao(): NoteDao
    abstract fun libraryBookDao(): LibraryBookDao
    abstract fun wishlistBookDao(): WishlistBookDao
    abstract fun wishlistBundleDao(): WishlistBundleDao
    abstract fun lentBookDao(): LentBookDao

    @DeleteColumn(tableName = "Book", columnName = "industry_identifier_type")
    class Migration3to4: AutoMigrationSpec
}