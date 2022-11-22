package com.guidofe.pocketlibrary.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
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
import com.guidofe.pocketlibrary.ui.modules.AddBookFab
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.LibraryListRow
import com.guidofe.pocketlibrary.ui.modules.Snackbars
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
    val context = LocalContext.current
    var isFabExpanded: Boolean by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var isFavoriteButtonFilled by remember { mutableStateOf(false) }
    var showDoubleIsbnDialog by remember { mutableStateOf(false) }
    var isbnToSearch: String? by remember { mutableStateOf(null) }
    var showConfirmDeleteBook by remember { mutableStateOf(false) }
    var showLendBookDialog by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(DpOffset.Zero) }
    val density = LocalDensity.current
    val selectionManager = vm.selectionManager
    var isMenuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(selectionManager.isMultipleSelecting) {
        if (selectionManager.isMultipleSelecting) {
            isFavoriteButtonFilled = false
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
                            showConfirmDeleteBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            stringResource(R.string.delete)
                        )
                    }
                    IconButton(
                        onClick = {
                            isFavoriteButtonFilled = !isFavoriteButtonFilled
                            vm.setFavoriteAndRefresh(
                                selectionManager.selectedKeys,
                                isFavoriteButtonFilled
                            )
                        }
                    ) {
                        if (isFavoriteButtonFilled)
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
                            onClick = { isMenuOpen = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = isMenuOpen,
                            onDismissRequest = { isMenuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.lend_books)) },
                                onClick = { showLendBookDialog = true }
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
    LaunchedEffect(isbnToSearch) {
        isbnToSearch?.let {
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
        Log.d("debug", "Drawing fab")
        vm.scaffoldState.fab = {
            AddBookFab(
                isExpanded = isFabExpanded,
                onMainFabClick = { isFabExpanded = !isFabExpanded },
                onDismissRequest = { isFabExpanded = false },
                onIsbnTyped = {
                    isbnToSearch = it
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
    LazyColumn {
        if (lazyPagingItems.loadState.refresh != LoadState.Loading &&
            lazyPagingItems.itemCount == 0
        )
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        stringResource(R.string.empty_library_text),
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
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
                    onRowLongPress = { pxOffset ->
                        with(density) {
                            val x = pxOffset.x.toDp()
                            val y = pxOffset.y.toDp() - 115.dp
                            offset = DpOffset(x, y)
                            itemDropdownOpen = true
                        }
                    }
                )
                DropdownMenu(
                    expanded = itemDropdownOpen,
                    onDismissRequest = { itemDropdownOpen = false },
                    offset = offset
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit_details)) },
                        onClick = {
                            navigator.navigate(EditBookPageDestination(item.value.info.bookId))
                        }
                    )
                    if (item.value.lent == null)
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.lend_book)) },
                            onClick = {
                                selectionManager.singleSelectedItem = item.value
                                itemDropdownOpen = false
                                showLendBookDialog = true
                            }
                        )
                    else
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.mark_as_returned)) },
                            onClick = {
                                itemDropdownOpen = false
                                vm.markLentBookAsReturned(item.value.lent)
                            }
                        )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            itemDropdownOpen = false
                            selectionManager.singleSelectedItem = item.value
                            showConfirmDeleteBook = true
                        }
                    )
                }
            }
        }
    }
    if (showDoubleIsbnDialog) {
        DuplicateIsbnDialog(
            onAddAnyway = {
                importVm.getImportedBooksFromIsbn(
                    vm.duplicateIsbn,
                    maxResults = 2,
                    callback = {
                        showDoubleIsbnDialog = false
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
                        showDoubleIsbnDialog = false
                    },
                )
            },
            onCancel = { showDoubleIsbnDialog = false }
        )
    }

    if (showConfirmDeleteBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                showConfirmDeleteBook = false
                if (selectionManager.isMultipleSelecting)
                    selectionManager.clearSelection()
            },
            isPlural = selectionManager.isMultipleSelecting && selectionManager.count > 1
        ) {
            if (selectionManager.isMultipleSelecting)
                vm.deleteSelectedBooksAndRefresh()
            else
                vm.deleteSelectedBookAndRefresh()
            showConfirmDeleteBook = false
        }
    }
    var whoString by remember { mutableStateOf("") }
    var lentDate by remember { mutableStateOf(LocalDate.now()) }
    var whoError by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    if (showLendBookDialog) {
        AlertDialog(
            onDismissRequest = { selectionManager.clearSelection(); showLendBookDialog = false },
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
                    showLendBookDialog = false
                }) {
                    Text(stringResource(R.string.ok_label))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectionManager.clearSelection()
                    showLendBookDialog = false
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
                            onClick = { showCalendar = true; showLendBookDialog = false; },
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
            onDismissed = { showLendBookDialog = true; showCalendar = false; },
            onDaySelected = { lentDate = it; showLendBookDialog = true; showCalendar = false; },
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
}