package com.guidofe.pocketlibrary.ui.pages

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
import com.guidofe.pocketlibrary.ui.modules.ConfirmDeleteBookDialog
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.booklogpage.BorrowedTab
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
    var tabIndex: Int by remember{mutableStateOf(0)}
    val borrowedList by vm.borrowedItems.collectAsState(initial = listOf())
    var isExpanded: Boolean by remember{mutableStateOf(false)}
    val isBorrowedMultipleSelecting by vm.borrowedSelectionManager.isMultipleSelecting.collectAsState()
    var showDoubleIsbnDialog by remember{mutableStateOf(false)}
    var isbnToSearch: String? by remember{mutableStateOf(null)}
    var showConfirmReturnBook by remember{mutableStateOf(false)}
    LaunchedEffect(tabIndex, isBorrowedMultipleSelecting) {
        if (tabIndex == 0 && isBorrowedMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.selecting),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.borrowedSelectionManager.clearSelection()
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
                            showConfirmReturnBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            stringResource(R.string.return_books)
                        )
                    }
                    Box() {
                        var isMoreMenuOpen by remember{mutableStateOf(false)}
                        IconButton(
                            onClick = {
                            }
                        ) {
                            Icon(
                                painterResource(R.drawable.more_vert_24px),
                                stringResource(R.string.more)
                            )
                        }
                        DropdownMenu(
                            expanded = isMoreMenuOpen,
                            onDismissRequest = { isMoreMenuOpen = false}
                        ) {
                            DropdownMenuItem(
                                {Text(stringResource(R.string.change_lender))},
                                {})
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
                    Snackbars.noBookFoundForIsbnSnackbar(importVm.snackbarHostState, context, scope) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.BORROWED)
                        )
                    }
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(importVm.snackbarHostState, context, scope){}
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
                    isExpanded = isExpanded,
                    onMainFabClick = { isExpanded = !isExpanded },
                    onDismissRequest = { isExpanded = false },
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
                        navigator.navigate(SearchBookOnlinePageDestination(BookDestination.BORROWED))
                    }
                )
            }
        }
        else {
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
                text = { Text(stringResource(R.string.lent)) }
            )
        }
        if (tabIndex == 0)
            BorrowedTab(
                borrowedList,
                vm.borrowedSelectionManager,
                updateBorrowed = {vm.updateBorrowed(it)},
                returnBorrowedBundle = {
                    vm.selectedBorrowedBook = it.bookBundle.book
                    showConfirmReturnBook = true
                }
            )
    }

    if(showConfirmReturnBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                showConfirmReturnBook = false
                if (isBorrowedMultipleSelecting)
                    vm.borrowedSelectionManager.clearSelection()
            },
            isPlural = isBorrowedMultipleSelecting && vm.borrowedSelectionManager.count > 1,
            messageSingular = stringResource(R.string.confirm_return_message),
            messagePlural = stringResource(R.string.confirm_return_message_plural),
        ) {
            if(isBorrowedMultipleSelecting)
                vm.deleteSelectedBorrowedBooks {}
            else
                vm.deleteSelectedBorrowedBook {}
            showConfirmReturnBook = false
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