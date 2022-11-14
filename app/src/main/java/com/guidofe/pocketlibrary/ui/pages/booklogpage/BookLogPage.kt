package com.guidofe.pocketlibrary.ui.pages.booklogpage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.AddBookFab
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.destinations.BookDisambiguationPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.SearchBookOnlinePageDestination
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.BookLogVM
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookLogVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination
@Composable
fun BookLogPage(
    navigator: DestinationsNavigator,
    vm: IBookLogVM = hiltViewModel<BookLogVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    disambiguationRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tabIndex: Int by remember { mutableStateOf(0) }
    val borrowedList by vm.borrowedItems.collectAsState(initial = emptyList())
    val lentList by vm.lentItems.collectAsState(initial = emptyList())
    var isFabExpanded: Boolean by remember { mutableStateOf(false) }
    var isBorrowTabMenuExpanded: Boolean by remember { mutableStateOf(false) }
    var isLentTabMenuExpanded: Boolean by remember { mutableStateOf(false) }
    var showDoubleIsbnDialog by remember { mutableStateOf(false) }
    var isbnToSearch: String? by remember { mutableStateOf(null) }
    LaunchedEffect(
        tabIndex,
        vm.borrowedTabState.selectionManager.isMultipleSelecting,
        vm.lentTabState.selectionManager.isMultipleSelecting
    ) {
        if (tabIndex == 0 && vm.borrowedTabState.selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.selecting),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.borrowedTabState.selectionManager.clearSelection()
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
                            vm.borrowedTabState.showConfirmReturnBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            stringResource(R.string.return_books)
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { isBorrowTabMenuExpanded = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = isBorrowTabMenuExpanded,
                            onDismissRequest = { isBorrowTabMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_lender)) },
                                onClick = {
                                    isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.isLenderDialogVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_start_date)) },
                                onClick = {
                                    isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.fieldToChange = BorrowedField.START
                                    vm.borrowedTabState.isCalendarVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_return_by_date)) },
                                onClick = {
                                    isBorrowTabMenuExpanded = false
                                    vm.borrowedTabState.fieldToChange = BorrowedField.RETURN_BY
                                    vm.borrowedTabState.isCalendarVisible = true
                                }
                            )
                        }
                    }
                }
            )
        } else if (tabIndex == 1 && vm.lentTabState.selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.selecting),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.lentTabState.selectionManager.clearSelection()
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
                            vm.removeLentStatus(
                                vm.lentTabState.selectionManager.selectedItems.value.values
                                    .mapNotNull {
                                        it.lent
                                    }
                            ) {}
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.return_book_24px),
                            stringResource(R.string.mark_as_returned),
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { isLentTabMenuExpanded = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = isLentTabMenuExpanded,
                            onDismissRequest = { isLentTabMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_borrower)) },
                                onClick = {
                                    isLentTabMenuExpanded = false
                                    vm.lentTabState.isBorrowerDialogVisible = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.change_start_date)) },
                                onClick = {
                                    isLentTabMenuExpanded = false
                                    vm.lentTabState.fieldToChange = LentField.START
                                    vm.lentTabState.isCalendarVisible = true
                                }
                            )
                        }
                    }
                }
            )
        } else {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.book_log)
            )
        }
    }
    LaunchedEffect(isbnToSearch) {
        isbnToSearch?.let {
            importVm.getAndSaveBookFromIsbnFlow(
                it,
                BookDestination.BORROWED,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(importVm.snackbarHostState, context, scope)
                },
                onNoBookFound = {
                    Snackbars.noBookFoundForIsbnSnackbar(
                        importVm.snackbarHostState, context, scope
                    ) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.BORROWED)
                        )
                    }
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(importVm.snackbarHostState, context, scope) {}
                },
                onMultipleBooksFound = { list ->
                    navigator.navigate(BookDisambiguationPageDestination(list.toTypedArray()))
                }
            )
        }
    }
    LaunchedEffect(tabIndex) {
        if (tabIndex == 0) {
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
                    }
                )
            }
        } else {
            vm.scaffoldState.fab = {}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = { Text(stringResource(R.string.borrowed)) }
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = { Text(stringResource(R.string.lent_tab)) }
            )
        }
        when (tabIndex) {
            0 -> {
                BorrowedTab(
                    borrowedList,
                    updateBorrowed = { vm.updateBorrowedBooks(it) },
                    deleteBorrowedBooks = { ids, callback ->
                        vm.deleteBorrowedBooks(ids, callback)
                    },
                    state = vm.borrowedTabState
                )
            }
            1 -> {
                LentTab(
                    lentItems = lentList,
                    updateLent = { vm.updateLent(it) },
                    removeLentStatus = { list, callback ->
                        vm.removeLentStatus(list, callback)
                    },
                    state = vm.lentTabState
                )
            }
        }
    }

    disambiguationRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(navResult.value, BookDestination.BORROWED) {
                Snackbars.bookSavedSnackbar(
                    importVm.snackbarHostState,
                    context,
                    scope,
                ) {}
            }
        }
    }
}