package com.guidofe.pocketlibrary.data.local.library_db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guidofe.pocketlibrary.data.local.library_db.converters.DateConverter
import com.guidofe.pocketlibrary.data.local.library_db.converters.UriConverter
import com.guidofe.pocketlibrary.data.local.library_db.daos.*
import com.guidofe.pocketlibrary.data.local.library_db.entities.*

@Database(entities = [
    Author::class,
    Book::class,
    BookAuthor::class,
    BookPlacement::class,
    BookGenre::class,
    Genre::class,
    Loan::class,
    Note::class,
    Place::class,
    com.guidofe.pocketlibrary.data.local.library_db.entities.Room::class,
    Bookshelf::class,
    Wishlist::class
], version = 3,
    exportSchema = true,
    autoMigrations = [
    ])
@TypeConverters(DateConverter::class, UriConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun bookAuthorDao(): BookAuthorDao
    abstract fun bookBundleDao(): BookBundleDao
    abstract fun bookDao(): BookDao
    abstract fun bookGenreDao(): BookGenreDao
    abstract fun bookPlacementDao(): BookPlacementDao
    abstract fun genreDao(): GenreDao
    abstract fun noteDao(): NoteDao
    abstract fun placeDao(): PlaceDao
    abstract fun roomDao(): RoomDao
    abstract fun bookshelfDao(): BookshelfDao
    abstract fun wishlistDao(): WishlistDao
}