package com.guidofe.pocketlibrary.ui.pages.library

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.repositories.LibraryFilter
import com.guidofe.pocketlibrary.ui.dialogs.*
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
@Destination(navArgsDelegate = LibraryPageNavArgs::class)
@Composable
fun LibraryPage(
    navigator: DestinationsNavigator,
    vm: ILibraryVM = hiltViewModel<LibraryVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    disambiguationRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>,
    filterRecipient: ResultRecipient<LibraryFilterPageDestination, LibraryFilter?>
) {
    val focusManager = LocalFocusManager.current
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = vm.state
    val fabFocusRequester = remember { FocusRequester() }
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            vm.state.isFabExpanded = false
        }
    }
    LaunchedEffect(vm.selectionManager.isMultipleSelecting) {
        if (vm.selectionManager.isMultipleSelecting) {
            state.isFavoriteButtonFilled = false
            vm.scaffoldState.refreshBar(
                title = { Text(stringResource(R.string.selecting)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.selectionManager.clearSelection()
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.backspace_24px),
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
                                vm.selectionManager.selectedKeys,
                                state.isFavoriteButtonFilled,
                            ) {}
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
                                        vm.selectionManager.clearSelection()
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.mark_as_read)) },
                                onClick = {
                                    vm.markSelectedBooksAsRead {
                                        vm.selectionManager.clearSelection()
                                    }
                                }
                            )
                        }
                    }
                }
            )
        } else {
            vm.scaffoldState.refreshBar(
                title = {

                    if (vm.searchFieldManager.isSearching) {
                        SearchField(
                            value = vm.searchFieldManager.searchField,
                            onValueChange = { vm.searchFieldManager.searchField = it },
                            shouldRequestFocus = vm.searchFieldManager.shouldSearchBarRequestFocus
                        ) {
                            vm.searchFieldManager.onSearchTriggered(focusManager)
                        }
                    } else {
                        Text(stringResource(R.string.library))
                    }
                },
                actions = {
                    if (!vm.searchFieldManager.isSearching) {
                        IconButton(
                            onClick = {
                                vm.searchFieldManager.isSearching = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search_24px),
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }
                    FilledIconToggleButton(
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        checked = vm.customQuery != null,
                        onCheckedChange = {
                            navigator.navigate(
                                LibraryFilterPageDestination(vm.customQuery)
                            )
                        },
                    ) {
                        Icon(
                            painterResource(R.drawable.filter_list_24px),
                            stringResource(R.string.filter),
                        )
                    }
                },
                navigationIcon = {
                    if (vm.searchFieldManager.isSearching) {
                        IconButton(
                            onClick = {
                                vm.searchFieldManager.onClosingSearch()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24px),
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                }
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
                },
                translationDialogState = vm.translationState
            )
        }
    }

    LaunchedEffect(Unit) {
        vm.invalidate()
        vm.scaffoldState.fab = {
            AddBookFab(
                isExpanded = state.isFabExpanded,
                onMainFabClick = {
                    state.isFabExpanded = !state.isFabExpanded
                    if (state.isFabExpanded)
                        fabFocusRequester.requestFocus()
                },
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
                },
                modifier = Modifier
                    .focusRequester(fabFocusRequester)
                    .onFocusChanged {
                        if (!it.hasFocus)
                            state.isFabExpanded = false
                    }
                    .focusable()
            )
        }
    }
    if (lazyPagingItems.loadState.refresh != LoadState.Loading &&
        lazyPagingItems.itemCount == 0
    )
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                stringResource(
                    if (vm.customQuery == null)
                        R.string.empty_library_text
                    else
                        R.string.no_book_match_search
                ),
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    LazyColumn(
        modifier = Modifier.nestedScroll(vm.scaffoldState.scrollBehavior!!.nestedScrollConnection),
        state = lazyListState,
    ) {
        itemsIndexed(
            items = lazyPagingItems,
            key = { _, item ->
                item.value.info.bookId
            }
        ) { index, item ->
            if (item == null)
                return@itemsIndexed
            Box {
                val bundle = item
                LibraryListRow(
                    item,
                    onRowTap = {
                        if (vm.selectionManager.isMultipleSelecting) {
                            vm.selectionManager.multipleSelectToggle(item.value)
                        } else
                            navigator.navigate(ViewBookPageDestination(item.value.info.bookId))
                    },
                    onCoverLongPress = {
                        if (!vm.selectionManager.isMultipleSelecting) {
                            vm.selectionManager.startMultipleSelection(item.value)
                        }
                    },
                    onRowLongPress = {
                        vm.selectionManager.singleSelectedItem = lazyPagingItems.peek(index)?.value
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
                                importVm.saveImportedBook(
                                    it[0], BookDestination.LIBRARY, vm.translationState
                                ) { id ->
                                    if (id > 0)
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
                if (vm.selectionManager.isMultipleSelecting)
                    vm.selectionManager.clearSelection()
            },
            isPlural = vm.selectionManager.isMultipleSelecting && vm.selectionManager.count > 1
        ) {
            if (vm.selectionManager.isMultipleSelecting)
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
                vm.selectionManager.clearSelection()
                state.showLendBookDialog = false
            },
            confirmButton = {
                Button(onClick = {
                    if (whoString.isBlank()) {
                        whoError = true
                        return@Button
                    }
                    if (vm.selectionManager.isMultipleSelecting)
                        vm.markSelectedBooksAsLent(whoString, lentDate) {
                            vm.selectionManager.clearSelection()
                        }
                    else {
                        vm.markSelectedBookAsLent(whoString, lentDate) {
                            vm.selectionManager.clearSelection()
                        }
                    }
                    state.showLendBookDialog = false
                }) {
                    Text(stringResource(R.string.ok_label))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.selectionManager.clearSelection()
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
            importVm.saveImportedBook(
                navResult.value, BookDestination.LIBRARY, vm.translationState
            ) {
                Snackbars.bookSavedSnackbar(
                    importVm.snackbarHostState,
                    context,
                    scope,
                ) {}
                vm.invalidate()
            }
        }
    }
    filterRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            vm.customQuery = navResult.value
            vm.invalidate()
        }
    }
    ModalBottomSheet(
        visible = state.isContextMenuVisible,
        onDismiss = {
            state.isContextMenuVisible = false
        }
    ) {
        vm.selectionManager.singleSelectedItem?.let { item ->
            RowWithIcon(
                icon = {
                    Icon(
                        painterResource(
                            if (item.info.isFavorite)
                                R.drawable.heart_24px
                            else
                                R.drawable.heart_filled_24px
                        ),
                        stringResource(
                            if (item.info.isFavorite)
                                R.string.remove_from_favorites
                            else
                                R.string.add_to_favorites
                        )
                    )
                },
                onClick = {
                    vm.setFavoriteAndRefresh(listOf(item.info.bookId), !item.info.isFavorite) {}
                    vm.selectionManager.singleSelectedItem = null
                    state.isContextMenuVisible = false
                }
            ) {
                Text(
                    stringResource(
                        if (item.info.isFavorite)
                            R.string.remove_from_favorites
                        else
                            R.string.add_to_favorites
                    )
                )
            }
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
            if (item.bookBundle.progress?.phase != ProgressPhase.READ) {
                RowWithIcon(
                    icon = {
                        Icon(
                            painterResource(R.drawable.check_24px),
                            stringResource(R.string.mark_as_read)
                        )
                    },
                    onClick = {
                        state.isContextMenuVisible = false
                        vm.markBookAsRead(item.bookBundle)
                    }
                ) {
                    Text(
                        stringResource(R.string.mark_as_read)
                    )
                }
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
    TranslationDialog(vm.translationState)
}