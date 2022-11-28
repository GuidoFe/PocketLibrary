package com.guidofe.pocketlibrary.viewmodels

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Note
import com.guidofe.pocketlibrary.data.local.library_db.entities.Progress
import com.guidofe.pocketlibrary.repositories.LocalRepository
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.ProgressTabState
import com.guidofe.pocketlibrary.ui.utils.ScaffoldState
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewBookVM @Inject constructor(
    val repo: LocalRepository,
    override val scaffoldState: ScaffoldState,
    override val snackbarHostState: SnackbarHostState,
) : ViewModel(), IViewBookVM {
    private var oldNote: Note? = null
    override var editedNote: String by mutableStateOf("")
    override val progTabState = ProgressTabState()
    override var bundle: BookBundle? by mutableStateOf(null)
        private set
    override fun initFromLocalBook(bookId: Long) {
        // TODO: What to do if book doesn't exist?
        viewModelScope.launch(Dispatchers.IO) {
            bundle = repo.getBookBundle(bookId)
            bundle?.let { bundle ->
                oldNote = bundle.note
                editedNote = oldNote?.note ?: ""
                progTabState.init(bundle.progress, bundle.book.pageCount)
            }
        }
    }

    override fun saveNote(callback: () -> Unit) {
        bundle?.book?.bookId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                if (id > 0) {
                    if (editedNote.isBlank() && oldNote != null) {
                        repo.deleteNote(oldNote!!)
                    } else {
                        if (editedNote.isNotBlank())
                            repo.upsertNote(Note(id, editedNote))
                    }
                }
                callback()
            }
        }
    }

    override fun saveProgress(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            progTabState.selectedPhase.let { phase ->
                if (phase == null)
                    bundle?.book?.bookId?.let { repo.deleteProgress(it) }
                else {
                    bundle?.book?.bookId?.let { id ->
                        val newProgress = Progress(
                            bookId = id,
                            phase = phase,
                            pagesRead = progTabState.pagesReadValue,
                            trackPages = progTabState.trackPages
                        )
                        repo.upsertProgress(newProgress)
                        if (bundle?.book?.pageCount != progTabState.totalPagesValue) {
                            repo.updatePageNumber(id, progTabState.totalPagesValue)
                            initFromLocalBook(id)
                        }
                    }
                }
            }
            callback()
        }
    }
}