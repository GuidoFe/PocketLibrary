package com.guidofe.pocketlibrary.model.repositories.local_db

import android.os.Parcelable
import android.util.Log
import androidx.room.*
import com.guidofe.pocketlibrary.model.repositories.local_db.converters.DateConverter
import com.guidofe.pocketlibrary.model.repositories.local_db.converters.UriConverter
import com.guidofe.pocketlibrary.model.repositories.local_db.daos.*
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.*
import com.guidofe.pocketlibrary.model.repositories.local_db.entities.Room

@Database(entities = [
    Author::class,
    Book::class,
    BookAuthor::class,
    BookPlacement::class,
    BookGenre::class,
    Bookshelf::class,
    Favorite::class,
    Genre::class,
    Loan::class,
    Note::class,
    Place::class,
    Room::class,
    Shelf::class,
    ShelfBook::class,
    Wishlist::class
], version = 2)
@TypeConverters(DateConverter::class, UriConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun bookAuthorDao(): BookAuthorDao
    abstract fun bookBundleDao(): BookBundleDao
    abstract fun bookDao(): BookDao
    abstract fun bookGenreDao(): BookGenreDao
    abstract fun bookPlacementDao(): BookPlacementDao
    abstract fun bookshelfDao(): BookshelfDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun genreDao(): GenreDao
    abstract fun noteDao(): NoteDao
    abstract fun placeDao(): PlaceDao
    abstract fun roomDao(): RoomDao
    abstract fun shelfDao(): ShelfDao
    abstract fun wishlistDao(): WishlistDao

    suspend fun insertBookBundle(bundle: BookBundle) {
        this.withTransaction {
            val bookId = bookDao().insert(bundle.book)
            val authorsToInsert: ArrayList<Author> = arrayListOf()
            val authorsId = arrayListOf<Long>()
            bundle.authors.forEach { author ->
                if (author.authorId == 0L)
                    authorsToInsert.add(author)
                else
                    authorsId.add(author.authorId)
            }
            if(authorsToInsert.isNotEmpty()) {
                val newIds = authorDao().insertAll(*authorsToInsert.toTypedArray())
                authorsId.addAll(newIds)
            }
            bookAuthorDao().insertAll(*(authorsId.map{id -> BookAuthor(bookId, id)}.toTypedArray()))
            val genresToInsert: ArrayList<Genre> = arrayListOf()
            val genresId = arrayListOf<Long>()
            bundle.genres.forEach { genre ->
                if (genre.genreId == 0L)
                    genresToInsert.add(genre)
                else
                    genresId.add(genre.genreId)
            }
            if(genresToInsert.isNotEmpty()) {
                val newIds = genreDao().insertAll(*genresToInsert.toTypedArray())
                genresId.addAll(newIds)
            }
            bookGenreDao().insertAll(*(genresId.map{id -> BookGenre(bookId, id)}.toTypedArray()))
            var placeId: Long? = null
            var roomId: Long? = null
            var bookshelfId: Long? = null
            var shelfId: Long? = null
            if(bundle.place != null)
                placeId = if (bundle.place.placeId == 0L)
                    placeDao().insert(bundle.place)
                else
                    bundle.place.placeId
            if (bundle.room != null)
                roomId = if (bundle.room.roomId == 0L)
                    roomDao().insert(bundle.room)
                else
                    bundle.room.roomId
            if (bundle.bookshelf != null)
                bookshelfId = if (bundle.bookshelf.bookshelfId == 0L)
                    bookshelfDao().insert(bundle.bookshelf)
                else
                    bundle.bookshelf.bookshelfId
            if (bundle.shelf != null)
                if (bundle.shelf.shelfId == 0L)
                    shelfDao().insert(bundle.shelf)
                else
                    bundle.shelf.shelfId
            if(placeId != null) {
                val placement = BookPlacement(bookId, placeId, roomId, bookshelfId, shelfId)
                bookPlacementDao().insert(placement)
            }
            if(bundle.isFavorite != null)
                favoriteDao().insert(Favorite(bookId))
            if(bundle.note != null)
                noteDao().insert(bundle.note.copy(bookId = bookId))
            if(bundle.inWishlist != null) {
                wishlistDao().insert(bundle.inWishlist.copy(bookId = bookId))
            }
        }
    }
}