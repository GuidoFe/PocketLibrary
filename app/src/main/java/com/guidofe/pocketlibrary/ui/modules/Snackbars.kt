package com.guidofe.pocketlibrary.ui.modules

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.guidofe.pocketlibrary.R
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class Snackbars {
    companion object {
        fun bookSavedSnackbar(
            hostState: SnackbarHostState,
            context: Context,
            scope: CoroutineScope,
            onDismiss: () -> Unit
        ) {
            scope.launch {
                val res = hostState.showSnackbar(
                    CustomSnackbarVisuals(
                        message = context.getString(R.string.books_saved),
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
            onActionPerformed: () -> Unit
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
                    onActionPerformed()
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
                        isError = false,
                        actionLabel = context.getString(R.string.add_anyway)
                    )
                )
                when (res) {
                    SnackbarResult.ActionPerformed -> onActionPerformed()
                    SnackbarResult.Dismissed -> onDismiss()
                }
            }
        }
    }
}