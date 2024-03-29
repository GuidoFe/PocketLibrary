package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.dialogs.ConfirmDeleteBookDialog
import com.guidofe.pocketlibrary.ui.dialogs.NotificationDialogFlow
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.utils.Menu
import com.guidofe.pocketlibrary.ui.utils.MenuItem
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.time.*
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedTab(
    borrowedItems: LazyPagingItems<SelectableListItem<BorrowedBundle>>,
    updateLender: (List<Long>, String?) -> Unit,
    updateStart: (List<Long>, Instant) -> Unit,
    updateEnd: (List<BorrowedBundle>, LocalDate?) -> Unit,
    updateNotification: (BorrowedBundle, Instant?) -> Unit,
    setReturnStatus: (Long, Boolean) -> Unit,
    moveToLibrary: (List<Long>) -> Unit,
    deleteBorrowedBooks: (bookIds: List<Long>, callback: () -> Unit) -> Unit,
    state: BorrowedTabState,
    navigator: DestinationsNavigator,
    setBottomSheetContent: (@Composable ColumnScope.() -> Unit) -> Unit,
    setBottomSheetVisibility: (Boolean, CoroutineScope) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionManager = state.selectionManager
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val windowInfo = rememberWindowInfo()
    val contextMenu = remember {
        Menu<BorrowedBundle>(
            menuItems = arrayOf(
                MenuItem(
                    labelId = {
                        if (it.info.isReturned)
                            R.string.mark_as_not_returned
                        else
                            R.string.mark_as_returned
                    },
                    iconId = {
                        if (it.info.isReturned)
                            R.drawable.upload_24px
                        else
                            R.drawable.book_hand_right_24px
                    },
                    onClick = { setReturnStatus(it.info.bookId, !it.info.isReturned) }
                ),
                MenuItem(
                    labelId = { R.string.details },
                    iconId = { R.drawable.info_24px },
                    onClick = { navigator.navigate(ViewBookPageDestination(it.info.bookId)) }
                ),
                MenuItem(
                    labelId = { R.string.edit },
                    iconId = { R.drawable.edit_24px },
                    onClick = { navigator.navigate(EditBookPageDestination(it.info.bookId)) }
                ),
                MenuItem(
                    labelId = { R.string.add_to_library },
                    iconId = { R.drawable.library_add_24px },
                    onClick = { moveToLibrary(listOf(it.info.bookId)) }
                ),
                MenuItem(
                    labelId = { R.string.delete },
                    iconId = { R.drawable.delete_24px },
                    onClick = {
                        selectionManager.singleSelectedItem = it
                        state.showConfirmDeleteBook = true
                    }
                )
            )
        )
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            state.isFabExpanded = false
        }
    }
    LaunchedEffect(Unit) {
        if (!windowInfo.isBottomSheetLayout())
            return@LaunchedEffect
        setBottomSheetContent {
            val selectedItem = selectionManager.singleSelectedItem
            selectedItem?.let { item ->
                for (line in contextMenu.menuItems) {
                    RowWithIcon(
                        icon = {
                            Icon(
                                painterResource(line.iconId(item)),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            setBottomSheetVisibility(false, coroutineScope)
                            line.onClick(item)
                            state.selectionManager.singleSelectedItem = null
                        }
                    ) {
                        Text(
                            stringResource(line.labelId(item))
                        )
                    }
                }
            }
        }
    }
    Column(modifier = modifier.fillMaxSize()) {
        if (borrowedItems.loadState.refresh != LoadState.Loading &&
            borrowedItems.itemCount == 0
        )
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    stringResource(R.string.you_have_no_borrowed),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
            }
        LazyColumn(
            state = lazyListState,
        ) {
            items(borrowedItems, key = { it.value.info.bookId }) { item ->
                if (item == null)
                    return@items
                var isDropdownMenuExpanded by remember { mutableStateOf(false) }
                var menuOffset by remember { mutableStateOf(DpOffset.Zero) }
                Box {
                    BorrowedBookRow(
                        item,
                        onRowTap = {
                            if (selectionManager.isMultipleSelecting)
                                selectionManager.multipleSelectToggle(item.value)
                            else {
                                navigator.navigate(ViewBookPageDestination(item.value.info.bookId))
                            }
                        },
                        onRowLongPress = { offset ->
                            selectionManager.singleSelectedItem = item.value
                            if (!selectionManager.isMultipleSelecting) {
                                if (windowInfo.isBottomSheetLayout()) {
                                    setBottomSheetVisibility(true, coroutineScope)
                                } else {
                                    menuOffset = offset
                                    isDropdownMenuExpanded = true
                                }
                            }
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
                        onNotificationIconClick = {
                            selectionManager.singleSelectedItem = item.value
                            state.isNotificationDialogVisible = true
                        }
                    ) {
                        if (!windowInfo.isBottomSheetLayout()) {
                            DropdownMenu(
                                expanded = isDropdownMenuExpanded,
                                onDismissRequest = { isDropdownMenuExpanded = false },
                                offset = menuOffset
                            ) {
                                for (line in contextMenu.menuItems) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(line.labelId(item.value))) },
                                        leadingIcon = {
                                            Icon(
                                                painterResource(line.iconId(item.value)),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            isDropdownMenuExpanded = false
                                            line.onClick(item.value)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Divider()
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
                        updateLender(
                            selectionManager.selectedItems.value.values.map {
                                it.info.bookId
                            },
                            textInput.ifBlank { null }
                        )
                    } else {
                        selectionManager.singleSelectedItem?.let {
                            state.isLenderDialogVisible = false
                            updateLender(listOf(it.info.bookId), textInput.ifBlank { null })
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
                    return@let LocalDateTime.ofInstant(it, ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()
                BorrowedField.RETURN_BY -> selectionManager.singleSelectedItem?.info?.end?.let {
                    return@let it
                } ?: selectionManager.singleSelectedItem?.info?.start?.let {
                    return@let LocalDateTime.ofInstant(it, ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()
                else -> LocalDate.now()
            }
        ) { newDate ->
            val convertedDate = newDate?.let {
                ZonedDateTime.of(
                    it, LocalTime.of(0, 0), ZoneId.systemDefault()
                ).toInstant()
            }
            if (state.fieldToChange == BorrowedField.START && convertedDate == null) {
                state.fieldToChange = null
                selectionManager.clearSelection()
                return@CalendarDialog
            }
            val bundles = if (selectionManager.isMultipleSelecting)
                selectionManager.selectedItems.value.values.toList()
            else
                selectionManager.singleSelectedItem?.let { listOf(it) } ?: emptyList()
            if (state.fieldToChange == BorrowedField.START) {
                updateStart(bundles.map { it.info.bookId }, convertedDate!!)
            } else {
                updateEnd(bundles, newDate)
            }
            state.isCalendarVisible = false
            selectionManager.clearSelection()
            state.fieldToChange = null
        }
    }

    if (state.showConfirmDeleteBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                state.showConfirmDeleteBook = false
                if (state.isMultipleSelecting)
                    selectionManager.clearSelection()
            },
            isPlural = state.isMultipleSelecting && selectionManager.count > 1,
            messageSingular = stringResource(R.string.confirm_delete_book),
            messagePlural = stringResource(R.string.confirm_delete_books),
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
            state.showConfirmDeleteBook = false
        }
    }

    if (state.isNotificationDialogVisible) {
        NotificationDialogFlow(
            onDismissRequest = { state.isNotificationDialogVisible = false },
            onConfirm = {
                selectionManager.singleSelectedItem?.let { book ->
                    updateNotification(book, it)
                    state.isNotificationDialogVisible = false
                }
            },
            startingEnabled = selectionManager.singleSelectedItem?.info?.notificationTime != null,
            startingInstant = selectionManager.singleSelectedItem?.info?.notificationTime
                ?: selectionManager.singleSelectedItem?.info?.end?.let { end ->
                    ZonedDateTime.of(
                        end,
                        LocalTime.of(8, 0),
                        ZoneId.systemDefault()
                    ).toInstant()
                } ?: Instant.now()
        )
    }
}