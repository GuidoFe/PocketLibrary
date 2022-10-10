package com.guidofe.pocketlibrary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Note
import com.guidofe.pocketlibrary.model.repositories.LibraryRepository
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewBookVM @Inject constructor(
    val repo: LibraryRepository,
    val appBarState: MutableStateFlow<AppBarState?>
): ViewModel(), IViewBookVM {
    private val _bundle = MutableStateFlow<BookBundle?>(null)
    override var bundle: StateFlow<BookBundle?> = _bundle.asStateFlow()
    override var editedNoteFlow: MutableStateFlow<String> = MutableStateFlow("")

    override fun initBundle(bookId: Long) {
        //TODO: What to do if book doesn't exist?
        viewModelScope.launch {
            _bundle.value = repo.getBookBundle(bookId)
            _bundle.value?.let { b ->
                b.note?.let { n ->
                    editedNoteFlow.value = n.note
                }

            }
        }
    }

    override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(appBarState)

    override fun saveNote() {
        viewModelScope.launch {
            _bundle.value?.let { b ->
                if (editedNoteFlow.value.isBlank() && b.note != null) {
                    repo.deleteNote(b.note)
                } else {
                    repo.upsertNote(Note(b.book.bookId, editedNoteFlow.value))
                }
            }
        }
    }

}