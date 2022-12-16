package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialog
import com.guidofe.pocketlibrary.ui.modules.AddBookFab
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.SearchField
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.destinations.BookDisambiguationPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.SearchBookOnlinePageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.WindowType
import com.guidofe.pocketlibrary.ui.utils.appBarColorAnimation
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.BookLogVM
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.previews.BookLogVMPreview
import com.guidofe.pocketlibrary.viewmodels.previews.ImportedBookVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun BookLogPage(
    navigator: DestinationsNavigator,
    vm: IBookLogVM = hiltViewModel<BookLogVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    disambiguationRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lentList by vm.lentItems.collectAsState(initial = emptyList())
    val focusManager = LocalFocusManager.current
    val fabFocusRequester = remember { FocusRequester() }
    val windowInfo = rememberWindowInfo()
    val lazyBorrowedPagingItems = vm.borrowedPager.collectAsLazyPagingItems()
    val appBarColor = appBarColorAnimation(vm.scaffoldState.scrollBehavior)
    LaunchedEffect(Unit) {
        vm.invalidateBorrowedPagingSource()
    }
    LaunchedEffect(
        vm.state.tabIndex,
        vm.borrowedSearchManager.isSearching,
        vm.lentSearchManager.isSearching,
        vm.borrowedTabState.selectionManager.isMultipleSelecting,
        vm.lentTabState.selectionManager.isMultipleSelecting
    ) {
        if (vm.state.tabIndex == 0 && vm.borrowedTabState.selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = { Text(stringResource(R.string.selecting)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.borrowedTabState.selectionManager.clearSelection()
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
                            vm.borrowedTabState.showConfirmDeleteBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            stringResource(R.string.delete_books)
                        )
                    }
                    IconButton(
                        onClick = {
                            vm.setStatusOfSelectedBorrowedBooks(true)
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            stringResource(R.string.return_books)
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { vm.state.isBorrowTabMenuExpanded = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = vm.state.isBorrowTabMenuExpanded,
                            onDismissRequest = { vm.state.isBorrowTabMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_lender)) },
                                onClick = {
                                    vm.state.isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.isLenderDialogVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_start_date)) },
                                onClick = {
                                    vm.state.isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.fieldToChange = BorrowedField.START
                                    vm.borrowedTabState.isCalendarVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.mark_as_returned)) },
                                onClick = {
                                    vm.state.isBorrowTabMenuExpanded = false
                                    vm.setStatusOfSelectedBorrowedBooks(true)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.mark_as_not_returned)) },
                                onClick = {
                                    vm.state.isBorrowTabMenuExpanded = false
                                    vm.setStatusOfSelectedBorrowedBooks(false)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_return_by_date)) },
                                onClick = {
                                    vm.state.isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.fieldToChange = BorrowedField.RETURN_BY
                                    vm.borrowedTabState.isCalendarVisible = true
                                }
                            )
                        }
                    }
                }
            )
        } else if (vm.state.tabIndex == 1 && vm.lentTabState.selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = { Text(stringResource(R.string.selecting)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.lentTabState.selectionManager.clearSelection()
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
                            vm.removeLentStatus(
                                vm.lentTabState.selectionManager.selectedItems.value.values
                                    .mapNotNull {
                                        it.lent
                                    }
                            ) {
                                coroutineScope.launch {
                                    vm.snackbarState.showSnackbar(
                                        CustomSnackbarVisuals(
                                            message = context.getString(
                                                R.string.books_moved_to_library
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.book_hand_left_24px),
                            stringResource(R.string.mark_as_returned),
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { vm.state.isLentTabMenuExpanded = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = vm.state.isLentTabMenuExpanded,
                            onDismissRequest = { vm.state.isLentTabMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_borrower)) },
                                onClick = {
                                    vm.state.isLentTabMenuExpanded = false
                                    vm.lentTabState.isBorrowerDialogVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_start_date)) },
                                onClick = {
                                    vm.state.isLentTabMenuExpanded = false
                                    vm.lentTabState.fieldToChange = LentField.START
                                    vm.lentTabState.isCalendarVisible = true
                                }
                            )
                        }
                    }
                }
            )
        } else {
            if (vm.currentSearchFieldManager().isSearching) {
                vm.scaffoldState.refreshBar(
                    title = {
                        SearchField(
                            value = vm.currentSearchFieldManager().searchField,
                            onValueChange = {
                                vm.currentSearchFieldManager().searchField = it
                            },
                            shouldRequestFocus = vm.currentSearchFieldManager()
                                .shouldSearchBarRequestFocus,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            vm.currentSearchFieldManager().onSearchTriggered(focusManager)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            vm.currentSearchFieldManager().onClosingSearch()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24px),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                )
            } else {
                vm.scaffoldState.refreshBar(
                    title = { Text(stringResource(R.string.book_log)) },
                    actions = {
                        IconButton(onClick = {
                            vm.currentSearchFieldManager().isSearching = true
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.search_24px),
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                        if (vm.state.tabIndex == 0) {
                            IconButton(onClick = {
                                vm.borrowedTabState.isMoreMenuOpen =
                                    !vm.borrowedTabState.isMoreMenuOpen
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert_24px),
                                    contentDescription = stringResource(R.string.more)
                                )
                            }
                            DropdownMenu(
                                expanded = vm.borrowedTabState.isMoreMenuOpen,
                                onDismissRequest = { vm.borrowedTabState.isMoreMenuOpen = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(
                                                if (vm.borrowedTabState.showReturnedBooks)
                                                    R.string.hideReturnedBooks
                                                else
                                                    R.string.showReturnedBooks
                                            )
                                        )
                                    },
                                    onClick = {
                                        vm.borrowedTabState.showReturnedBooks =
                                            !vm.borrowedTabState.showReturnedBooks
                                        vm.borrowedTabState.isMoreMenuOpen = false
                                        vm.invalidateBorrowedPagingSource()
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
    LaunchedEffect(vm.state.isbnToSearch) {
        vm.state.isbnToSearch?.let {
            importVm.getAndSaveBookFromIsbnFlow(
                it,
                BookDestination.BORROWED,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(
                        importVm.snackbarHostState,
                        context,
                        coroutineScope
                    )
                },
                onNoBookFound = {
                    Snackbars.noBookFoundForIsbnSnackbar(
                        importVm.snackbarHostState, context, coroutineScope
                    ) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.BORROWED)
                        )
                    }
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(
                        importVm.snackbarHostState,
                        context,
                        coroutineScope
                    ) {}
                },
                onMultipleBooksFound = { list ->
                    navigator.navigate(BookDisambiguationPageDestination(list.toTypedArray()))
                },
                translationDialogState = vm.translationState
            )
        }
    }
    LaunchedEffect(vm.state.tabIndex) {
        if (vm.state.tabIndex == 0 && windowInfo.screenWidthInfo < WindowType.EXTENDED) {
            vm.scaffoldState.fab = {
                AddBookFab(
                    isExpanded = vm.borrowedTabState.isFabExpanded,
                    onMainFabClick = {
                        vm.borrowedTabState.isFabExpanded = !vm.borrowedTabState.isFabExpanded
                        if (vm.borrowedTabState.isFabExpanded)
                            fabFocusRequester.requestFocus()
                    },
                    onDismissRequest = { vm.borrowedTabState.isFabExpanded = false },
                    onIsbnTyped = {
                        vm.state.isbnToSearch = it
                    },
                    onInsertManually = {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.BORROWED)
                        )
                    },
                    onScanIsbn = {
                        navigator.navigate(ScanIsbnPageDestination(BookDestination.BORROWED))
                    },
                    onSearchOnline = {
                        navigator.navigate(
                            SearchBookOnlinePageDestination(BookDestination.BORROWED)
                        )
                    },
                    modifier = Modifier
                        .focusRequester(fabFocusRequester)
                        .onFocusChanged {
                            if (!it.hasFocus)
                                vm.borrowedTabState.isFabExpanded = false
                        }
                        .focusable()
                )
            }
        } else {
            vm.scaffoldState.fab = {}
        }
    }
    if (windowInfo.screenWidthInfo < WindowType.EXTENDED) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(vm.scaffoldState.scrollBehavior.nestedScrollConnection)
        ) {
            TabRow(
                selectedTabIndex = vm.state.tabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = appBarColor.value
            ) {
                Tab(
                    selected = vm.state.tabIndex == 0,
                    onClick = { vm.state.tabIndex = 0 },
                    text = { Text(stringResource(R.string.borrowed)) }
                )
                Tab(
                    selected = vm.state.tabIndex == 1,
                    onClick = { vm.state.tabIndex = 1 },
                    text = { Text(stringResource(R.string.lent_tab)) }
                )
            }
            when (vm.state.tabIndex) {
                0 -> {
                    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    BorrowedTab(
                        lazyBorrowedPagingItems,
                        setReturnStatus = { id, isReturned ->
                            vm.setBookReturnStatus(id, isReturned)
                        },
                        updateBorrowed = { vm.updateBorrowedBooks(it) },
                        deleteBorrowedBooks = { ids, callback ->
                            vm.deleteBorrowedBooks(ids, callback)
                        },
                        moveToLibrary = { ids ->
                            vm.moveBorrowedBooksToLibrary(ids)
                        },
                        navigator = navigator,
                        state = vm.borrowedTabState,
                        setBottomSheetContent = { vm.scaffoldState.bottomSheetContent = it },
                        setBottomSheetVisibility = { visibility, scope ->
                            vm.scaffoldState.setBottomSheetVisibility(visibility, scope)
                        }
                    )
                }
                1 -> {
                    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    LentTab(
                        lentItems = lentList,
                        updateLent = { vm.updateLent(it) },
                        removeLentStatus = { list, callback ->
                            vm.removeLentStatus(list, callback)
                        },
                        state = vm.lentTabState,
                        navigator = navigator,
                        setBottomSheetContent = { vm.scaffoldState.bottomSheetContent = it },
                        setBottomSheetVisibility = { visibility, scope ->
                            vm.scaffoldState.setBottomSheetVisibility(visibility, scope)
                        }
                    )
                }
            }
        }
    } else {
        vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Row {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    Text(
                        stringResource(R.string.borrowed_books),
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    BorrowedTab(
                        lazyBorrowedPagingItems,
                        setReturnStatus = { id, isReturned ->
                            vm.setBookReturnStatus(id, isReturned)
                        },
                        updateBorrowed = { vm.updateBorrowedBooks(it) },
                        deleteBorrowedBooks = { ids, callback ->
                            vm.deleteBorrowedBooks(ids, callback)
                        },
                        moveToLibrary = { ids ->
                            vm.moveBorrowedBooksToLibrary(ids)
                        },
                        navigator = navigator,
                        state = vm.borrowedTabState,
                        setBottomSheetContent = { vm.scaffoldState.bottomSheetContent = it },
                        setBottomSheetVisibility = { visibility, scope ->
                            vm.scaffoldState.setBottomSheetVisibility(visibility, scope)
                        }
                    )
                }
                AddBookFab(
                    isExpanded = vm.borrowedTabState.isFabExpanded,
                    onMainFabClick = {
                        vm.borrowedTabState.isFabExpanded = !vm.borrowedTabState.isFabExpanded
                        if (vm.borrowedTabState.isFabExpanded)
                            fabFocusRequester.requestFocus()
                    },
                    onDismissRequest = { vm.borrowedTabState.isFabExpanded = false },
                    onIsbnTyped = {
                        vm.state.isbnToSearch = it
                    },
                    onInsertManually = {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.BORROWED)
                        )
                    },
                    onScanIsbn = {
                        navigator.navigate(ScanIsbnPageDestination(BookDestination.BORROWED))
                    },
                    onSearchOnline = {
                        navigator.navigate(
                            SearchBookOnlinePageDestination(BookDestination.BORROWED)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset((-16).dp, (-16).dp)
                        .focusRequester(fabFocusRequester)
                        .onFocusChanged {
                            if (!it.hasFocus)
                                vm.borrowedTabState.isFabExpanded = false
                        }
                        .focusable()

                )
            }
            Divider(Modifier.width(1.dp).fillMaxHeight())
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    stringResource(R.string.lent_books),
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(8.dp)
                )
                LentTab(
                    lentItems = lentList,
                    updateLent = { vm.updateLent(it) },
                    removeLentStatus = { list, callback ->
                        vm.removeLentStatus(list, callback)
                    },
                    state = vm.lentTabState,
                    navigator = navigator,
                    setBottomSheetContent = { vm.scaffoldState.bottomSheetContent = it },
                    setBottomSheetVisibility = { visibility, scope ->
                        vm.scaffoldState.setBottomSheetVisibility(visibility, scope)
                    }
                )
            }
        }
    }

    disambiguationRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(
                navResult.value, BookDestination.BORROWED, vm.translationState
            ) {
                Snackbars.bookSavedSnackbar(
                    importVm.snackbarHostState,
                    context,
                    coroutineScope,
                ) {}
            }
        }
    }
    TranslationDialog(vm.translationState)
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
private fun BookLogPagePreview() {
    PocketLibraryTheme() {
        BookLogPage(
            EmptyDestinationsNavigator,
            vm = BookLogVMPreview(),
            importVm = ImportedBookVMPreview(),
            EmptyResultRecipient()
        )
    }
}