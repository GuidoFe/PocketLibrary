package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class BookLogVM @Inject constructor(
    repo: LocalRepository
): ViewModel(), IBookLogVM {
    override var selectedBorrowedBook: Book? = null
    override val borrowedSelectionManager = MultipleSelectionManager<Long, BorrowedBundle>(
        getKey = {it.info.bookId}
    )

    override val borrowedItems = repo.getBorrowedBundles()
        .combine(borrowedSelectionManager.selectedItems) { books, selected ->
            books.map{
                SelectableListItem(it, selected.containsKey(it.info.bookId))
            }
        }
}