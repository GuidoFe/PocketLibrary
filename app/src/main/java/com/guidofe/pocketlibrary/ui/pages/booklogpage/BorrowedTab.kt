package com.guidofe.pocketlibrary.ui.pages.booklogpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.dialogs.ConfirmDeleteBookDialog
import com.guidofe.pocketlibrary.ui.modules.BorrowedBookRow
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedTab(
    borrowedItems: List<SelectableListItem<BorrowedBundle>>,
    updateBorrowed: (List<BorrowedBook>) -> Unit,
    deleteBorrowedBooks: (bookIds: List<Long>, callback: () -> Unit) -> Unit,
    state: BorrowedTabState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val selectionManager = state.selectionManager
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier) {
        LazyColumn {
            if (borrowedItems.isEmpty())
                item { Text(stringResource(R.string.empty_library_text)) }
            items(borrowedItems, key = { it.value.info.bookId }) { item ->
                Box {
                    BorrowedBookRow(
                        item,
                        onRowTap = {
                            if (selectionManager.isMultipleSelecting)
                                selectionManager.multipleSelectToggle(item.value)
                        },
                        onCoverLongPress = {
                            if (!selectionManager.isMultipleSelecting)
                                selectionManager.startMultipleSelection(item.value)
                        },
                        onLenderTap = {
                            state.fieldToChange = BorrowedField.LENDER
                            selectionManager.singleSelectedItem = item.value
                            state.isLenderDialogVisible = true
                        },
                        onStartTap = {
                            state.fieldToChange = BorrowedField.START
                            selectionManager.singleSelectedItem = item.value
                            state.isCalendarVisible = true
                        },
                        onReturnByTap = {
                            state.fieldToChange = BorrowedField.RETURN_BY
                            selectionManager.singleSelectedItem = item.value
                            state.isCalendarVisible = true
                        },
                        areButtonsActive = !selectionManager.isMultipleSelecting,
                        onSwiped = {
                            selectionManager.singleSelectedItem = item.value
                            state.showConfirmReturnBook = true
                        },
                        swipeThreshold = (config.screenWidthDp / 3).dp
                    )
                }
            }
        }
    }

    if (state.isLenderDialogVisible) {
        var textInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                state.fieldToChange = null
                selectionManager.clearSelection()
                state.isLenderDialogVisible = false
            },
            confirmButton = {
                Button(onClick = {
                    if (selectionManager.isMultipleSelecting) {
                        updateBorrowed(
                            selectionManager.selectedItems.value.values.map {
                                it.info.copy(who = textInput.ifBlank { null })
                            }
                        )
                    } else {
                        selectionManager.singleSelectedItem?.let {
                            state.isLenderDialogVisible = false
                            val newBorrowed = it.info.copy(
                                who = textInput.ifBlank { null }
                            )
                            updateBorrowed(listOf(newBorrowed))
                        }
                    }
                    selectionManager.clearSelection()
                    state.fieldToChange = null
                    state.isLenderDialogVisible = false
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.fieldToChange = null
                    selectionManager.clearSelection()
                    state.isLenderDialogVisible = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = {
                if (!state.selectionManager.isMultipleSelecting)
                    Text(selectionManager.singleSelectedItem?.bookBundle?.book?.title ?: "???")
            },
            text = {
                Column {
                    val description = when (state.fieldToChange) {
                        BorrowedField.LENDER -> stringResource(R.string.lender_colon)
                        BorrowedField.START -> stringResource(R.string.start_colon)
                        BorrowedField.RETURN_BY -> stringResource(R.string.return_by_colon)
                        else -> "???"
                    }
                    Text(stringResource(R.string.lender_colon))
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                    )
                }
            }
        )
    }
    if (state.isCalendarVisible) {
        CalendarDialog(
            onDismissed = {
                state.isCalendarVisible = false
                selectionManager.clearSelection()
                state.fieldToChange = null
            },
            hasClearOption = state.fieldToChange == BorrowedField.RETURN_BY,
            startingDate = when (state.fieldToChange) {
                BorrowedField.START -> selectionManager.singleSelectedItem?.info?.start?.let {
                    return@let LocalDate.parse(it.toString())
                } ?: LocalDate.now()
                BorrowedField.RETURN_BY -> selectionManager.singleSelectedItem?.info?.end?.let {
                    return@let LocalDate.parse(it.toString())
                } ?: selectionManager.singleSelectedItem?.info?.start?.let {
                    return@let LocalDate.parse(it.toString())
                } ?: LocalDate.now()
                else -> LocalDate.now()
            }
        ) { newDate ->
            val convertedDate = newDate?.let { Date.valueOf(newDate.toString()) }
            if (state.fieldToChange == BorrowedField.START && convertedDate == null) {
                state.fieldToChange = null
                selectionManager.clearSelection()
                return@CalendarDialog
            }

            if (state.isMultipleSelecting) {
                val list = if (state.fieldToChange == BorrowedField.START) {
                    selectionManager.selectedItems.value.values.map {
                        it.info.copy(start = convertedDate!!)
                    }
                } else {
                    selectionManager.selectedItems.value.values.map {
                        it.info.copy(end = convertedDate)
                    }
                }
                updateBorrowed(list)
            } else {
                val info = when (state.fieldToChange) {
                    BorrowedField.START -> selectionManager.singleSelectedItem?.info?.copy(
                        start = convertedDate!!
                    )
                    BorrowedField.RETURN_BY -> selectionManager.singleSelectedItem?.info?.copy(
                        end = convertedDate
                    )
                    else -> null
                }
                info?.let { updateBorrowed(listOf(it)) }
            }
            state.isCalendarVisible = false
            selectionManager.clearSelection()
            state.fieldToChange = null
        }
    }

    if (state.showConfirmReturnBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                state.showConfirmReturnBook = false
                if (state.isMultipleSelecting)
                    selectionManager.clearSelection()
            },
            isPlural = state.isMultipleSelecting && selectionManager.count > 1,
            messageSingular = stringResource(R.string.confirm_return_message),
            messagePlural = stringResource(R.string.confirm_return_message_plural),
        ) {
            if (state.isMultipleSelecting)
                deleteBorrowedBooks(selectionManager.selectedKeys) {
                    selectionManager.clearSelection()
                }
            else {
                selectionManager.singleSelectedItem?.let {
                    deleteBorrowedBooks(listOf(it.info.bookId)) {}
                }
            }
            state.showConfirmReturnBook = false
        }
    }
}