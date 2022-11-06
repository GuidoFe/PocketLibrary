package com.guidofe.pocketlibrary.ui.pages.librarypage

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import com.guidofe.pocketlibrary.viewmodels.LibraryVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.guidofe.pocketlibrary.utils.BookDestination
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

//TODO: Undo delete action

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
    var isExpanded: Boolean by remember{mutableStateOf(false)}
    val scope = rememberCoroutineScope()
    val isMultipleSelecting by vm.selectionManager.isMultipleSelecting.collectAsState()
    var isFavoriteButtonFilled by remember{mutableStateOf(false)}
    var showDoubleIsbnDialog by remember{mutableStateOf(false)}
    var isbnToSearch: String? by remember{mutableStateOf(null)}
    var showConfirmDeleteBook by remember{mutableStateOf(false)}
    var showLendBookDialog by remember{mutableStateOf(false)}
    var offset by remember{mutableStateOf(Offset.Zero)}
    val screenDensity = LocalDensity.current
    LaunchedEffect(isMultipleSelecting) {
        if (isMultipleSelecting) {
            isFavoriteButtonFilled = false
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.selecting),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            vm.selectionManager.clearSelection()
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
                                vm.selectionManager.selectedKeys,
                                isFavoriteButtonFilled
                            )
                        }
                    ) {
                        if(isFavoriteButtonFilled)
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
                    Snackbars.noBookFoundForIsbnSnackbar(importVm.snackbarHostState, context, scope) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.LIBRARY)
                        )
                    }
                },
                onOneBookSaved = {
                    Snackbars.bookSavedSnackbar(importVm.snackbarHostState, context, scope){
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
                isExpanded = isExpanded,
                onMainFabClick = { isExpanded = !isExpanded },
                onDismissRequest = { isExpanded = false},
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
        items(
            items = lazyPagingItems,
            key = {it.value.info.bookId}
        ) { item ->
            if (item == null)
                return@items
            Box {
                var itemDropdownOpen by remember { mutableStateOf(false) }
                LibraryListRow(
                    item,
                    onRowTap = {
                        if (isMultipleSelecting) {
                            vm.selectionManager.multipleSelectToggle(item.value)
                        } else
                            navigator.navigate(ViewBookPageDestination(item.value.info.bookId))
                    },
                    onCoverLongPress = {
                        if (!isMultipleSelecting) {
                            vm.selectionManager.startMultipleSelection(item.value)
                        }
                    },
                    onRowLongPress = {
                        offset = it
                        itemDropdownOpen = true
                    }
                )
                DropdownMenu(
                    expanded = itemDropdownOpen,
                    onDismissRequest = { itemDropdownOpen = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit_details)) },
                        onClick = {
                            navigator.navigate(EditBookPageDestination(item.value.info.bookId))
                        }
                    )
                    if (item.value.lent == null)
                        DropdownMenuItem(
                            text = {Text(stringResource(R.string.lend_book))},
                            onClick = {
                                vm.selectedBook = item.value.bookBundle.book
                                itemDropdownOpen = false
                                showLendBookDialog = true
                            }
                        )
                    else
                        DropdownMenuItem(
                            text = {Text(stringResource(R.string.mark_as_returned))},
                            onClick = {
                                itemDropdownOpen = false
                                vm.markLentBookAsReturned(item.value.lent)
                            })
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            itemDropdownOpen = false
                            vm.selectedBook = item.value.bookBundle.book
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
            onCancel = {showDoubleIsbnDialog = false})
    }

    if(showConfirmDeleteBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                showConfirmDeleteBook = false
                if (isMultipleSelecting)
                    vm.selectionManager.clearSelection()
            },
            isPlural = isMultipleSelecting && vm.selectionManager.count > 1
        ) {
            if(isMultipleSelecting)
                vm.deleteSelectedBooksAndRefresh()
            else
                vm.deleteSelectedBookAndRefresh()
            showConfirmDeleteBook = false
        }
    }
    var whoString by remember{mutableStateOf("")}
    var lentDate by remember{mutableStateOf(LocalDate.now())}
    var whoError by remember{mutableStateOf(false)}
    var showCalendar by remember{mutableStateOf(false)}
    if(showLendBookDialog) {
        AlertDialog(
            onDismissRequest = {vm.selectedBook = null; showLendBookDialog = false},
            confirmButton = {
                Button(onClick = {
                    if (whoString.isBlank()){
                        whoError = true
                        return@Button
                    }
                    if (isMultipleSelecting)
                        vm.markSelectedItemsAsLent(whoString, lentDate)
                    else {
                        vm.markSelectedBookAsLent(whoString, lentDate)
                    }
                    showLendBookDialog = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = {vm.selectedBook = null; showLendBookDialog = false}) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = whoString,
                        onValueChange = {whoString = it},
                        label = {Text(stringResource(R.string.to_whom))},
                        isError = whoError,
                        supportingText = {if (whoError) Text(stringResource(R.string.please_enter_value))}
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(stringResource(R.string.lent_on))
                        AssistChip(
                            onClick = {showCalendar = true; showLendBookDialog = false; },
                            label = {Text(lentDate.format(
                                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            )}
                        )
                    }
                }
            }
        )
    }
    if(showCalendar) {
        CalendarDialog(
            onDismissed = {showLendBookDialog = true; showCalendar = false; },
            onDaySelected = {lentDate = it; showLendBookDialog = true; showCalendar = false;},
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