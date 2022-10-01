package com.guidofe.pocketlibrary.model.repositories

import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.*

class FakeLibraryRepository: LibraryRepository {
    private val books = mutableMapOf<Long, Book>()
    private val authors = mutableMapOf<Long, Author>()
    private val bookAuthors = mutableSetOf<BookAuthor>()
    private val places = mutableMapOf<Long, Place>()
    private val rooms = mutableMapOf<Long, Room>()
    private val bookshelves = mutableMapOf<Long, Bookshelf>()
    private val bookPlacements = mutableMapOf<Long, BookPlacement>()
    private val notes = mutableMapOf<Long, Note>()
    private val genres = mutableMapOf<Long, Genre>()
    private val bookGenres = mutableListOf<BookGenre>()
    private var lastBookIndex = 0L
    private var lastAuthorIndex = 0L
    private var lastPlaceIndex = 0L
    private var lastRoomIndex = 0L
    private var lastBookshelfIndex = 0L
    private var lastGenresIndex = 0L

    private fun updateLastIndex(oldIndex: Long, newIndex: Long): Long {
        return oldIndex.coerceAtLeast(newIndex)
    }

    private fun updateLastIndex(oldIndex: Long, newIndexes: List<Long>): Long {
        val maxIndex = newIndexes.max()
        return oldIndex.coerceAtLeast(maxIndex)
    }
    // Simulates an ignore conflict strategy
    private fun <T> insertElement(
        element: T,
        table: MutableMap<Long, T>,
        lastIndex: Long,
        getIndex: (T) -> Long,
        setIndex: (T, Long) -> T
    ): Long {
        var index = lastIndex
        return if (getIndex(element) == 0L) {
            index++
            val newElement = setIndex(element, index)
            table[index] = newElement
            index
        } else {
            if (table.keys.contains(getIndex(element)))
                -1
            else {
                table[getIndex(element)] = element
                getIndex(element)
            }
        }
    }

    private fun <T> insertAllElements(
        elements: List<T>,
        table: MutableMap<Long, T>,
        lastIndex: Long,
        getIndex: (T) -> Long,
        setIndex: (T, Long) -> T
    ): List<Long> {
        var index = lastIndex
        val indexList = mutableListOf<Long>()
        elements.forEach { element ->
            val newIndex = insertElement(element, table, index, getIndex, setIndex)
            indexList.add(newIndex)
            index = updateLastIndex(index, newIndex)
        }
        return indexList
    }

    override suspend fun insertBookBundle(bundle: BookBundle): Long? {
        var bookId: Long = -1L
        bookId = insertBook(bundle.book)
        val authorsToInsert: ArrayList<Author> = arrayListOf()
        val authorsId = arrayListOf<Long>()
        bundle.authors.forEach { author ->
            if (author.authorId == 0L)
                authorsToInsert.add(author)
            else
                authorsId.add(author.authorId)
        }
        if(authorsToInsert.isNotEmpty()) {
            val newIds = insertAllAuthors(authorsToInsert)
            authorsId.addAll(newIds)
        }
        insertAllBookAuthors(authorsId.map{id -> BookAuthor(bookId, id) })
        val genresToInsert: ArrayList<Genre> = arrayListOf()
        val genresId = arrayListOf<Long>()
        bundle.genres.forEach { genre ->
            if (genre.genreId == 0L)
                genresToInsert.add(genre)
            else
                genresId.add(genre.genreId)
        }
        if(genresToInsert.isNotEmpty()) {
            val newIds = insertAllGenres(genresToInsert)
            genresId.addAll(newIds)
        }
        insertAllBookGenres(genresId.map{id -> BookGenre(bookId, id) })
        var placeId: Long? = null
        var roomId: Long? = null
        var bookshelfId: Long? = null
        if(bundle.place != null)
            placeId = if (bundle.place!!.placeId == 0L)
                insertPlace(bundle.place!!)
            else
                bundle.place!!.placeId
        if (bundle.room != null)
            roomId = if (bundle.room!!.roomId == 0L)
                insertRoom(bundle.room!!)
            else
                bundle.room!!.roomId
        if (bundle.bookshelf != null)
            bookshelfId = if (bundle.bookshelf!!.bookshelfId == 0L)
                insertBookshelf(bundle.bookshelf!!)
            else
                bundle.bookshelf!!.bookshelfId
        if(placeId != null) {
            val placement = BookPlacement(bookId, placeId, roomId, bookshelfId)
            insertBookPlacement(placement)
        }
        if(bundle.note != null)
            upsertNote(bundle.note!!.copy(bookId = bookId))
        return bookId
    }
    override suspend fun getBookBundle(bookId: Long): BookBundle {
        val book = books[bookId]!!
        val authorList = bookAuthors
            .filter { it.bookId == book.bookId }
            .map{ba -> authors[ba.authorId]!!}
        val genresList = bookGenres
            .filter { it.bookId == book.bookId}
            .map{ga -> genres[ga.genreId]!!}
        val placement = bookPlacements[book.bookId]
        var place: Place? = null
        var room: Room? = null
        var bookshelf: Bookshelf? = null
        if (placement != null) {
            place = places[placement.placeId]
            if (placement.roomId != null)
                room = rooms[placement.roomId]
            if (placement.bookshelfId != null)
                bookshelf = bookshelves[placement.bookshelfId]
        }
        return BookBundle(
            book,
            authorList,
            genresList,
            place,
            room,
            bookshelf,
            notes.getOrDefault(bookId, null)
        )
    }

    override suspend fun getBookBundles(pageNumber: Int, pageSize: Int): List<BookBundle> {
        val start = pageNumber * pageSize
        if (start >= books.size)
            return listOf()
        var end = start + pageSize
        if (end > books.size)
            end = books.size
        val subset = books.keys.toList().subList(start, end)
        return subset.map {id -> getBookBundle(id)}
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return block()
    }

    override suspend fun insertBook(book: Book): Long {
        val index = insertElement<Book>(
            book,
            books,
            lastBookIndex,
            {it.bookId},
            {b, id -> b.copy(bookId = id)}
        )
        lastBookIndex = updateLastIndex(lastBookIndex, index)
        return index
    }

    override suspend fun updateBook(book: Book) {
        if (books.containsKey(book.bookId))
            books[book.bookId] = book
    }

    override suspend fun insertAllAuthors(newAuthors: List<Author>): List<Long> {
        val indexes = insertAllElements<Author>(newAuthors, authors, lastAuthorIndex, {it.authorId}, {a, i -> a.copy(authorId = i)})
        lastAuthorIndex = updateLastIndex(lastAuthorIndex, indexes)
        return indexes
    }

    override suspend fun getExistingAuthors(authorsNames: List<String>): List<Author> {
        return authors.filter{authorsNames.contains((it.value.name))}.values.toList()
    }

    override suspend fun insertAllBookAuthors(newBookAuthors: List<BookAuthor>) {
        bookAuthors.addAll(newBookAuthors)
    }

    override suspend fun insertPlace(place: Place): Long {
        val index = insertElement<Place>(
            place,
            places,
            lastPlaceIndex,
            {place.placeId},
            {p, i -> p.copy(placeId = i)}
        )
        lastPlaceIndex = updateLastIndex(lastPlaceIndex, index)
        return index
    }

    override suspend fun getPlaceIdByName(name: String): Long? {
        val ids: List<Long> = places.values.filter{it.name == name}.map{it.placeId}
        return if (ids.isNotEmpty())
            ids[0]
        else null
    }

    override suspend fun insertRoom(room: Room): Long {
        val index = insertElement<Room>(
            room,
            rooms,
            lastRoomIndex,
            {it.roomId},
            {r, i -> r.copy(roomId = i)}
        )
        lastRoomIndex = updateLastIndex(lastRoomIndex, index)
        return index
    }

    override suspend fun getRoomIdByNameAndPlaceId(name: String, placeId: Long): Long? {
        val ids: List<Long> = rooms.values.filter{
            it.name == name && it.parentPlace == placeId
        }.map{it.roomId}
        return if (ids.isNotEmpty())
            ids[0]
        else
            null
    }

    override suspend fun insertBookshelf(bookshelf: Bookshelf): Long {
        val index = insertElement<Bookshelf>(
            bookshelf,
            bookshelves,
            lastBookshelfIndex,
            {it.bookshelfId},
            {b, i -> b.copy(bookshelfId = i)}
        )
        lastBookshelfIndex = updateLastIndex(lastBookshelfIndex, index)
        return index
    }

    override suspend fun getBookshelfIdByNameAndRoomId(name: String, roomId: Long): Long? {
        val ids: List<Long> = bookshelves.values.filter{
            it.name == name && it.parentRoom == roomId
        }.map{it.bookshelfId}
        return if (ids.isNotEmpty())
            ids[0]
        else
            null
    }

    override suspend fun insertBookPlacement(bookPlacement: BookPlacement) {
        bookPlacements[bookPlacement.bookId] = bookPlacement
    }

    override suspend fun upsertNote(note: Note) {
        notes[note.bookId] = note
    }

    override suspend fun insertAllGenres(newGenres: List<Genre>): List<Long> {
        val ids = insertAllElements<Genre>(
            newGenres,
            genres,
            lastGenresIndex,
            {it.genreId},
            {g, i -> g.copy(genreId = i)}
        )
        lastGenresIndex = updateLastIndex(lastGenresIndex, ids)
        return ids
    }

    override suspend fun getGenresByNames(names: List<String>): List<Genre> {
        return genres.values.filter { names.contains(it.name) }
    }

    override suspend fun insertAllBookGenres(newBookGenres: List<BookGenre>) {
        bookGenres.addAll(newBookGenres)
    }

    override fun close() {

    }
}