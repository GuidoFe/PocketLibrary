package com.guidofe.pocketlibrary.ui.pages.booklogpage

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.Book
import com.guidofe.pocketlibrary.data.local.library_db.entities.BorrowedBook
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.modules.BorrowedBookRow
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import java.sql.Date
import java.time.LocalDate

private enum class BorrowedField{LENDER, START, RETURN_BY}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedTab(
    borrowedItems: List<SelectableListItem<BorrowedBundle>>,
    selectionManager: MultipleSelectionManager<Long, BorrowedBundle>,
    updateBorrowed: (BorrowedBook) -> Unit,
    returnBorrowedBundle: (BorrowedBundle) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLenderDialogVisible by remember{mutableStateOf(false)}
    var clickedField: BorrowedField? by remember{ mutableStateOf(null) }
    var clickedItem: BorrowedBundle? by remember{ mutableStateOf(null) }

    Column(modifier = modifier) {
        LazyColumn() {
            items(borrowedItems, key = {it.value.info.bookId}) {item ->
                Box(
                ) {
                    BorrowedBookRow(
                        item,
                        onRowTap = {
                            if (selectionManager.isMultipleSelecting.value)
                                selectionManager.multipleSelectToggle(item.value)
                        },
                        onCoverLongPress = {
                            if (!selectionManager.isMultipleSelecting.value)
                                selectionManager.startMultipleSelection(item.value)
                        },
                        onLenderTap = {
                            clickedField = BorrowedField.LENDER
                            clickedItem = item.value
                            isLenderDialogVisible = true
                        },
                        onStartTap = {
                            clickedField = BorrowedField.START
                            clickedItem = item.value
                            isLenderDialogVisible = true
                        },
                        onReturnByTap = {
                            clickedField = BorrowedField.RETURN_BY
                            clickedItem = item.value
                            isLenderDialogVisible = true
                        },
                        onDelete = {
                            returnBorrowedBundle(it)
                        }
                    )
                }
            }
        }
    }

    if (isLenderDialogVisible) {
        if (clickedField == BorrowedField.LENDER) {
            var textInput by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = {
                    clickedField = null
                    clickedItem = null
                    isLenderDialogVisible = false
                },
                confirmButton = {
                    Button(onClick = {
                        clickedItem?.let {
                            isLenderDialogVisible = false
                            updateBorrowed(it.info.copy(
                                who = textInput.ifBlank { null }
                            ))
                        }
                    }) { Text(stringResource(R.string.save)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        clickedField = null
                        clickedItem = null
                        isLenderDialogVisible = false
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                title = { Text(clickedItem?.bookBundle?.book?.title ?: "???") },
                text = {
                    Column {
                        val description = when (clickedField) {
                            BorrowedField.LENDER -> stringResource(R.string.lender_colon)
                            BorrowedField.START -> stringResource(R.string.start_colon)
                            BorrowedField.RETURN_BY -> stringResource(R.string.return_by_colon)
                            else -> "???"
                        }
                        Text(description)
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                        )
                    }
                }
            )
        } else {
            CalendarDialog(
                onDismissed = {isLenderDialogVisible = false},
                hasClearOption = clickedField == BorrowedField.RETURN_BY,
                onClear = {
                    if (clickedField == BorrowedField.RETURN_BY) {
                        val info = clickedItem?.info?.copy(end = null)
                        info?.let{updateBorrowed(it)}
                    }
                    isLenderDialogVisible = false
                },
                startingDate = when (clickedField) {
                    BorrowedField.START -> clickedItem?.info?.start?.let {
                        return@let LocalDate.parse(it.toString())
                    }?: LocalDate.now()
                    BorrowedField.RETURN_BY -> clickedItem?.info?.end?.let {
                        return@let LocalDate.parse(it.toString())
                    }?: clickedItem?.info?.start?.let{return@let LocalDate.parse(it.toString())}?: LocalDate.now()
                    else -> LocalDate.now()
                }
            ) { newDate ->
                val info = when (clickedField) {
                    BorrowedField.START -> clickedItem?.info?.copy(start = Date.valueOf(newDate.toString()))
                    BorrowedField.RETURN_BY -> clickedItem?.info?.copy(end = Date.valueOf(newDate.toString()))
                    else -> null
                }
                info?.let{updateBorrowed(it)}
                isLenderDialogVisible = false
            }
        }
    }


}