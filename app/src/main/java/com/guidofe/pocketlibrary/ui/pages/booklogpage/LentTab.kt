package com.guidofe.pocketlibrary.ui.pages.booklogpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.modules.LentBookRow
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LentTab(
    lentItems: List<SelectableListItem<LibraryBundle>>,
    updateLent: (List<LentBook>) -> Unit,
    removeLentStatus: (books: List<LentBook>, callback: () -> Unit) -> Unit,
    state: LentTabState,
    modifier: Modifier = Modifier
) {
    val selectionManager = state.selectionManager
    Column(modifier = modifier) {
        LazyColumn {
            if (lentItems.isEmpty())
                item { Text(stringResource(R.string.empty_library_text)) }
            items(lentItems, key = { it.value.info.bookId }) { item ->
                Box {
                    LentBookRow(
                        item,
                        onRowTap = {
                            if (selectionManager.isMultipleSelecting)
                                selectionManager.multipleSelectToggle(item.value)
                        },
                        onCoverLongPress = {
                            if (!selectionManager.isMultipleSelecting)
                                selectionManager.startMultipleSelection(item.value)
                        },
                        onBorrowerTap = {
                            state.fieldToChange = LentField.BORROWER
                            selectionManager.singleSelectedItem = item.value
                            state.isBorrowerDialogVisible = true
                        },
                        onStartTap = {
                            state.fieldToChange = LentField.START
                            selectionManager.singleSelectedItem = item.value
                            state.isCalendarVisible = true
                        },
                        onMarkAsReturned = {
                            it.lent?.let { lent ->
                                removeLentStatus(listOf(lent)) {}
                            }
                        },
                        areButtonsActive = !selectionManager.isMultipleSelecting
                    )
                }
            }
        }
    }

    if (state.isBorrowerDialogVisible) {
        var textInput by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                state.fieldToChange = null
                selectionManager.clearSelection()
                state.isBorrowerDialogVisible = false
            },
            confirmButton = {
                Button(onClick = {
                    if (textInput.isBlank()) {
                        isError = true
                        return@Button
                    }
                    if (selectionManager.isMultipleSelecting) {
                        updateLent(
                            selectionManager.selectedItems.value.values.mapNotNull {
                                it.lent?.copy(who = textInput)
                            }
                        )
                    } else {
                        selectionManager.singleSelectedItem?.let { selected ->
                            state.isBorrowerDialogVisible = false
                            val newLent = selected.lent?.copy(
                                who = textInput
                            )
                            newLent?.let { updateLent(listOf(it)) }
                        }
                    }
                    selectionManager.clearSelection()
                    state.fieldToChange = null
                    state.isBorrowerDialogVisible = false
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.fieldToChange = null
                    selectionManager.clearSelection()
                    state.isBorrowerDialogVisible = false
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
                    Text(stringResource(R.string.lent_to_colon))
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        isError = isError,
                        supportingText = {
                            if (isError) Text(stringResource(R.string.please_enter_value))
                        }
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
            startingDate = if (!state.isMultipleSelecting) {
                state.selectionManager.singleSelectedItem?.lent?.start?.let {
                    LocalDate.parse(it.toString())
                } ?: LocalDate.now()
            } else LocalDate.now()
        ) { newDate ->
            val convertedDate = newDate?.let { Date.valueOf(newDate.toString()) }
            if (convertedDate == null) {
                state.fieldToChange = null
                selectionManager.clearSelection()
                return@CalendarDialog
            }

            if (state.isMultipleSelecting) {
                val list = selectionManager.selectedItems.value.values.mapNotNull {
                    it.lent?.copy(start = convertedDate)
                }
                updateLent(list)
            } else {
                val info = selectionManager.singleSelectedItem?.lent?.copy(
                    start = convertedDate
                )
                info?.let { updateLent(listOf(it)) }
            }
            state.isCalendarVisible = false
            selectionManager.clearSelection()
            state.fieldToChange = null
        }
    }
}