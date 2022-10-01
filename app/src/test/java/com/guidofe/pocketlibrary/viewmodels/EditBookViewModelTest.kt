package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Author
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.Genre
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.model.repositories.FakeLibraryRepository
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.pages.editbookpage.FormData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditBookViewModelTest {
    private lateinit var vm: EditBookViewModel
    private lateinit var db: LibraryRepository

    @Before
    fun setup() {
        db = FakeLibraryRepository()
        vm = EditBookViewModel(
            db,
            SavedStateHandle(),
            MutableStateFlow(AppBarState())
        )
    }
    // =============================================================================================
    // IMPORTED BOOK
    @Test
    fun `initialize from imported book`() {
        vm.initializeFromImportedBook(ImportedBookData("A"))
        assertThat(vm.formData.title).isEqualTo("A")
    }

    @Test
    fun `bookId is 0 if from imported book`() {
        vm.initializeFromImportedBook(ImportedBookData("A"))
        assertThat(vm.currentBookId).isEqualTo(0L)
    }
//TODO: implement this test
/*    @Test
    fun `http cover of imported book is converted in https`() {
        vm.initializeFromImportedBook(ImportedBookData("A", coverUrl = "http://image.jpeg"))
        assertThat(vm.formData.coverUri?.toString()?.lowercase()).startsWith("https")
    }*/

    //==============================================================================================
    //DATABASE

    @Test
    fun `initialize from database`() = runTest(UnconfinedTestDispatcher()) {
        vm.initialiseFromDatabase(
            BookBundle(
                Book(1, title="A"),
                authors = listOf(Author(1, "a"), Author(2, "b")),
                genres = listOf(Genre(1, "g1"), Genre(2, "g2")),
        ))
        assertThat(vm.formData.title).isEqualTo("A")
    }

    @Test
    fun `initialize from database gives correct id`() = runTest(UnconfinedTestDispatcher()) {
        vm.initialiseFromDatabase(
            BookBundle(
                Book(1, title="A"),
            ))
        assertThat(vm.currentBookId).isEqualTo(1L)
    }

    //==============================================================================================
    //Submit book

    @Test
    fun `submit book save formData to db`() = runTest(UnconfinedTestDispatcher()) {
        vm.formData = FormData(title = "A")
        vm.submitBook()
        assertThat(db.getBookBundles(pageSize = 5).size).isEqualTo(1)
    }
}