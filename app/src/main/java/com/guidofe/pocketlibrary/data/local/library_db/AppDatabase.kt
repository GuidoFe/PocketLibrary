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
    BookAuthor::class,
    BookGenre::class,
    Genre::class,
    Loan::class,
    Note::class,
    Wishlist::class
], version = 5,
    exportSchema = true,
    autoMigrations = [])
@TypeConverters(DateConverter::class, UriConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun bookAuthorDao(): BookAuthorDao
    abstract fun bookBundleDao(): BookBundleDao
    abstract fun bookDao(): BookDao
    abstract fun bookGenreDao(): BookGenreDao
    abstract fun genreDao(): GenreDao
    abstract fun noteDao(): NoteDao
    abstract fun wishlistDao(): WishlistDao

    @DeleteColumn(tableName = "Book", columnName = "industry_identifier_type")
    class Migration3to4: AutoMigrationSpec
}