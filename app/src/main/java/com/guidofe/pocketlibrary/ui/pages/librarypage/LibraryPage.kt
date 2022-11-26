package com.guidofe.pocketlibrary.ui.pages.librarypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.dialogs.ConfirmDeleteBookDialog
import com.guidofe.pocketlibrary.ui.dialogs.DuplicateIsbnDialog
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.LibraryVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// TODO: Undo delete action

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun LibraryPage(
    navigator: DestinationsNavigator,
    vm: ILibraryVM = hiltViewModel<LibraryVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    disambiguationRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>,
) {
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val selectionManager = vm.selectionManager
    val state = vm.state

    LaunchedEffect(selectionManager.isMultipleSelecting) {
        if (selectionManager.isMultipleSelecting) {
            state.isFavoriteButtonFilled = false
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.selecting),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            selectionManager.clearSelection()
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_back_24px),
                            stringResource(R.string.clear_selection)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            state.showConfirmDeleteBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            stringResource(R.string.delete)
                        )
                    }
                    IconButton(
                        onClick = {
                            state.isFavoriteButtonFilled = !state.isFavoriteButtonFilled
                            vm.setFavoriteAndRefresh(
                                selectionManager.selectedKeys,
                                state.isFavoriteButtonFilled
                            )
                        }
                    ) {
                        if (state.isFavoriteButtonFilled)
                            Icon(
                                painterResource(R.drawable.heart_filled_24px),
                                stringResource(R.string.remove_from_favorites)
                            )
                        else
                            Icon(
                                painterResource(R.drawable.heart_24px),
                                stringResource(R.string.add_to_favorites)
                            )
                    }
                    Box {
                        IconButton(
                            onClick = { state.isMenuOpen = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = state.isMenuOpen,
                            onDismissRequest = { state.isMenuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.lend_books)) },
                                onClick = { state.showLendBookDialog = true }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.mark_as_returned)) },
                                onClick = {
                                    vm.markSelectedLentBooksAsReturned {
                                        selectionManager.clearSelection()
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } else {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.library)
            )
        }
    }
    LaunchedEffect(state.isbnToSearch) {
        state.isbnToSearch?.let {
            importVm.getAndSaveBookFromIsbnFlow(
                it,
                BookDestination.LIBRARY,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(importVm.snackbarHostState, context, scope)
                },
                onNoBookFound = {
                    Snackbars.noBookFoundForIsbnSnackbar(
                        importVm.snackbarHostState, context, scope
                    ) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.LIBRARY)
                        )
                    }
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(importVm.snackbarHostState, context, scope) {
                        vm.invalidate()
                    }
                },
                onMultipleBooksFound = { list ->
                    navigator.navigate(BookDisambiguationPageDestination(list.toTypedArray()))
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        vm.invalidate()
        vm.scaffoldState.fab = {
            AddBookFab(
                isExpanded = state.isFabExpanded,
                onMainFabClick = { state.isFabExpanded = !state.isFabExpanded },
                onDismissRequest = { state.isFabExpanded = false },
                onIsbnTyped = {
                    state.isbnToSearch = it
                },
                onInsertManually = {
                    navigator.navigate(
                        EditBookPageDestination(newBookDestination = BookDestination.LIBRARY)
                    )
                },
                onScanIsbn = {
                    navigator.navigate(ScanIsbnPageDestination())
                },
                onSearchOnline = {
                    navigator.navigate(SearchBookOnlinePageDestination(BookDestination.LIBRARY))
                }
            )
        }
    }
    if (lazyPagingItems.loadState.refresh != LoadState.Loading &&
        lazyPagingItems.itemCount == 0
    )
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.empty_library_text),
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    LazyColumn {
        items(
            items = lazyPagingItems,
            key = { it.value.info.bookId }
        ) { item ->
            if (item == null)
                return@items
            Box {
                var itemDropdownOpen by remember { mutableStateOf(false) }
                LibraryListRow(
                    item,
                    onRowTap = {
                        if (selectionManager.isMultipleSelecting) {
                            selectionManager.multipleSelectToggle(item.value)
                        } else
                            navigator.navigate(ViewBookPageDestination(item.value.info.bookId))
                    },
                    onCoverLongPress = {
                        if (!selectionManager.isMultipleSelecting) {
                            selectionManager.startMultipleSelection(item.value)
                        }
                    },
                    onRowLongPress = {
                        vm.selectionManager.singleSelectedItem = item.value
                        state.isContextMenuVisible = true
                    }
                )
            }
        }
    }
    if (state.showDoubleIsbnDialog) {
        DuplicateIsbnDialog(
            onAddAnyway = {
                importVm.getImportedBooksFromIsbn(
                    vm.duplicateIsbn,
                    maxResults = 2,
                    callback = {
                        state.showDoubleIsbnDialog = false
                        when (it.size) {
                            0 -> {
                                scope.launch {
                                    vm.snackbarHostState.showSnackbar(
                                        CustomSnackbarVisuals(
                                            context.getString(R.string.no_book_found),
                                            true
                                        )
                                    )
                                }
                            }
                            1 -> {
                                importVm.saveImportedBook(it[0], BookDestination.LIBRARY) { id ->
                                    navigator.navigate(ViewBookPageDestination(id))
                                }
                            }
                            else -> navigator.navigate(
                                BookDisambiguationPageDestination(it.toTypedArray())
                            )
                        }
                    },
                    failureCallback = {
                        scope.launch {
                            vm.snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    it,
                                    true
                                )
                            )
                        }
                        state.showDoubleIsbnDialog = false
                    },
                )
            },
            onCancel = { state.showDoubleIsbnDialog = false }
        )
    }

    if (state.showConfirmDeleteBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                state.showConfirmDeleteBook = false
                if (selectionManager.isMultipleSelecting)
                    selectionManager.clearSelection()
            },
            isPlural = selectionManager.isMultipleSelecting && selectionManager.count > 1
        ) {
            if (selectionManager.isMultipleSelecting)
                vm.deleteSelectedBooksAndRefresh()
            else
                vm.deleteSelectedBookAndRefresh()
            state.showConfirmDeleteBook = false
        }
    }
    var whoString by remember { mutableStateOf("") }
    var lentDate by remember { mutableStateOf(LocalDate.now()) }
    var whoError by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    if (state.showLendBookDialog) {
        AlertDialog(
            onDismissRequest = {
                selectionManager.clearSelection()
                state.showLendBookDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    if (whoString.isBlank()) {
                        whoError = true
                        return@Button
                    }
                    if (selectionManager.isMultipleSelecting)
                        vm.markSelectedItemsAsLent(whoString, lentDate) {
                            selectionManager.clearSelection()
                        }
                    else {
                        vm.markSelectedBookAsLent(whoString, lentDate) {
                            selectionManager.clearSelection()
                        }
                    }
                    state.showLendBookDialog = false
                }) {
                    Text(stringResource(R.string.ok_label))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectionManager.clearSelection()
                    state.showLendBookDialog = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = whoString,
                        onValueChange = { whoString = it },
                        label = { Text(stringResource(R.string.to_whom)) },
                        isError = whoError,
                        supportingText = {
                            if (whoError) Text(stringResource(R.string.please_enter_value))
                        }
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(stringResource(R.string.lent_on))
                        AssistChip(
                            onClick = { showCalendar = true; state.showLendBookDialog = false; },
                            label = {
                                Text(
                                    lentDate.format(
                                        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        )
    }
    if (showCalendar) {
        CalendarDialog(
            onDismissed = { state.showLendBookDialog = true; showCalendar = false; },
            onDaySelected = {
                lentDate = it; state.showLendBookDialog = true; showCalendar = false
            },
            startingDate = lentDate,
        )
    }
    disambiguationRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(navResult.value, BookDestination.LIBRARY) {
                Snackbars.bookSavedSnackbar(
                    importVm.snackbarHostState,
                    context,
                    scope,
                ) {}
                vm.invalidate()
            }
        }
    }
    ModalBottomSheet(
        visible = state.isContextMenuVisible,
        onDismiss = { state.isContextMenuVisible = false }
    ) {
        val selectedItem = selectionManager.singleSelectedItem
        selectedItem?.let { item ->
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(
                            if (item.lent == null)
                                R.drawable.book_hand_right_24px
                            else
                                R.drawable.book_hand_left_24px
                        ),
                        stringResource(
                            if (item.lent == null)
                                R.string.lend_book
                            else
                                R.string.mark_as_returned
                        )
                    )
                },
                onClick = {
                    if (item.lent == null)
                        state.showLendBookDialog = true
                    else {
                        vm.markLentBookAsReturned(item.lent)
                        vm.selectionManager.singleSelectedItem = null
                    }
                    state.isContextMenuVisible = false
                }
            ) {
                Text(
                    stringResource(
                        if (item.lent == null)
                            R.string.lend_book
                        else
                            R.string.mark_as_returned
                    )
                )
            }
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(R.drawable.info_24px),
                        stringResource(R.string.details)
                    )
                },
                onClick = {
                    state.isContextMenuVisible = false
                    navigator.navigate(ViewBookPageDestination(item.info.bookId))
                }
            ) {
                Text(
                    stringResource(R.string.details)
                )
            }
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(R.drawable.edit_24px),
                        stringResource(R.string.edit)
                    )
                },
                onClick = {
                    state.isContextMenuVisible = false
                    navigator.navigate(EditBookPageDestination(item.info.bookId))
                }
            ) {
                Text(
                    stringResource(R.string.edit)
                )
            }
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(R.drawable.delete_24px),
                        stringResource(R.string.delete)
                    )
                },
                onClick = {
                    state.showConfirmDeleteBook = true
                    state.isContextMenuVisible = false
                }
            ) {
                Text(
                    stringResource(R.string.delete)
                )
            }
        }
    }
}