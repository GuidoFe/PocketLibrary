package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.entities.*
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ViewBookImmutableData
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewBookVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState
): ViewModel(), IViewBookVM {
    private var oldNote: Note? = null
    override var editedNote: String by mutableStateOf("")
    override var data: ViewBookImmutableData? by mutableStateOf(null)
        private set
    override fun initFromLibraryBook(bookId: Long) {
        //TODO: What to do if book doesn't exist?
        viewModelScope.launch {
            val bookBundle = repo.getBookBundle(bookId)
            bookBundle?.let { bundle ->
                data = ViewBookImmutableData(bundle)
                oldNote = bundle.note
                editedNote = oldNote?.note ?: ""
            }
        }
    }

    override fun saveNote(bookId: Long) {
        viewModelScope.launch {
            if(bookId > 0) {
                if (editedNote.isBlank() && oldNote != null) {
                    repo.deleteNote(oldNote!!)
                } else {
                    if(editedNote.isNotBlank())
                        repo.upsertNote(Note(bookId, editedNote))
                }
            }
        }
    }

}