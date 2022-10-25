package com.guidofe.pocketlibrary.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryVM @Inject constructor(
    private val repo: LibraryRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState
): ViewModel(), ILibraryVM {
    override val selectionManager = MultipleSelectionManager<Long, BookBundle>(
        getKey = {it.book.bookId}
    )
    override val bookListFlow: Flow<List<SelectableListItem<BookBundle>>> =
        repo.getBookBundles().combine(this.selectionManager.selectedItems) { items, selected ->
            items.map {
                SelectableListItem(it, selected.containsKey(it.book.bookId))
            }
        }

    override fun onCleared() {
        Log.d("debug", "cleared")
        super.onCleared()
    }

    override fun deleteSelectedBooks() {

        viewModelScope.launch {
            repo.deleteBooksByIds(selectionManager.selectedKeys)
            selectionManager.clearSelection()
        }
    }

    override fun clearSelection() {
        selectionManager.clearSelection()
    }

    override fun deleteBook(book: Book) {
        viewModelScope.launch {
            repo.deleteBook(book)
        }
    }

    override fun setFavorite(ids: List<Long>, favorite: Boolean) {
        viewModelScope.launch {
            if (ids.isEmpty()) return@launch
            repo.updateFavorite(ids, favorite)
        }
    }
}
