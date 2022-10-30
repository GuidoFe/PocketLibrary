package com.guidofe.pocketlibrary.ui.modules

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.guidofe.pocketlibrary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class Snackbars {
    companion object {
        fun bookSavedSnackbar(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            areMultipleBooks: Boolean = false,
            onDismiss: () -> Unit
        ) {
            scope.launch {
                val res = hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = if (areMultipleBooks)
                            context.getString(R.string.books_saved)
                        else
                            context.getString(R.string.book_saved),
                        isError = false
                    )
                )
                if (res == SnackbarResult.Dismissed) {
                    onDismiss()
                }
            }
        }

        fun connectionErrorSnackbar(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope
        ) {
            scope.launch {
                hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = context.getString(R.string.error_no_connection),
                        isError = true
                    )
                )
            }
        }

        fun noBookFoundForIsbnSnackbar(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            onInsertManuallyAction: () -> Unit
        ) {
            scope.launch {
                val res = hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = context.getString(R.string.no_book_found),
                        isError = true,
                        actionLabel = context.getString(R.string.insert_manually)
                    )
                )
                if (res == SnackbarResult.ActionPerformed) {
                    onInsertManuallyAction()
                }
            }
        }

        fun bookAlreadyPresentSnackbar(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            onDismiss: () -> Unit,
            onActionPerformed: () -> Unit,
        ) {
            scope.launch {
                val res = hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = context.getString(R.string.book_alredy_present_message),
                        isError = true,
                        actionLabel = context.getString(R.string.add_anyway)
                    )
                )
                when (res) {
                    SnackbarResult.ActionPerformed -> onActionPerformed()
                    SnackbarResult.Dismissed -> onDismiss()
                }
            }
        }

        fun bookAlreadyPresentSnackbarWithTitle(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            bookTitle: String,
            onDismiss: () -> Unit,
            onActionPerformed: () -> Unit,
        ) {
            scope.launch {
                val res = hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = context.getString(
                                R.string.book_alredy_present_message_with_title
                        ).format(bookTitle),
                        isError = true,
                        actionLabel = context.getString(R.string.add_anyway)
                    )
                )
                when (res) {
                    SnackbarResult.ActionPerformed -> onActionPerformed()
                    SnackbarResult.Dismissed -> onDismiss()
                }
            }
        }

        fun bookMovedToLibrary(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            areMultipleBooks: Boolean = false,
        ) {
            scope.launch {
                hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = if (areMultipleBooks)
                            context.getString(R.string.books_moved_to_library)
                        else
                            context.getString(R.string.book_moved_to_library)
                    )
                )
            }
        }
    }
}