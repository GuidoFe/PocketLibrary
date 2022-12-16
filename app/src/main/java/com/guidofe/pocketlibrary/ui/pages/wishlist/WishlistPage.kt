package com.guidofe.pocketlibrary.ui.pages.wishlist

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.ConfirmDeleteBookDialog
import com.guidofe.pocketlibrary.ui.dialogs.DuplicateIsbnDialog
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialog
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.WishlistPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

// TODO: Undo delete action

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun WishlistPage(
    navigator: DestinationsNavigator,
    vm: IWishlistPageVM = hiltViewModel<WishlistPageVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>(),
    disambiguationRecipient: ResultRecipient<BookDisambiguationPageDestination, ImportedBookData>,
) {
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    val fabFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            vm.state.isFabExpanded = false
        }
    }
// TODO add to borrowed
    LaunchedEffect(vm.selectionManager.isMultipleSelecting, vm.searchFieldManager.isSearching) {
        if (vm.selectionManager.isMultipleSelecting) {
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
                            vm.moveSelectedBooksToLibraryAndRefresh {
                                Snackbars.bookMovedToLibrary(
                                    vm.snackbarHostState,
                                    context,
                                    scope,
                                    vm.selectionManager.count > 1
                                )
                            }
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.place_item_24px),
                            stringResource(R.string.add_to_library)
                        )
                    }
                    IconButton(
                        onClick = {
                            vm.state.showConfirmDeleteBook = true
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            stringResource(R.string.delete)
                        )
                    }
                }
            )
        } else {
            if (vm.searchFieldManager.isSearching) {
                vm.scaffoldState.refreshBar(
                    title = {
                        SearchField(
                            value = vm.searchFieldManager.searchField,
                            onValueChange = { vm.searchFieldManager.searchField = it },
                            shouldRequestFocus = vm.searchFieldManager.shouldSearchBarRequestFocus
                        ) {
                            vm.searchFieldManager.onSearchTriggered(focusManager)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            vm.searchFieldManager.onClosingSearch()
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24px),
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                )
            } else {
                vm.scaffoldState.refreshBar(
                    title = {
                        Text(stringResource(R.string.wishlist))
                    },
                    actions = {
                        IconButton(
                            onClick = { vm.searchFieldManager.isSearching = true }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search_24px),
                                contentDescription = stringResource(R.string.search)
                            )
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
                BookDestination.WISHLIST,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(importVm.snackbarHostState, context, scope)
                },
                onNoBookFound = {
                    Snackbars.noBookFoundForIsbnSnackbar(
                        vm.snackbarHostState, context, scope
                    ) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.WISHLIST)
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
        Log.d("debug", "Drawing fab")
        vm.invalidate()
        vm.scaffoldState.fab = {
            AddBookFab(
                isExpanded = vm.state.isFabExpanded,
                onMainFabClick = {
                    vm.state.isFabExpanded = !vm.state.isFabExpanded
                    if (vm.state.isFabExpanded)
                        fabFocusRequester.requestFocus()
                },
                onDismissRequest = { vm.state.isFabExpanded = false },
                onIsbnTyped = {
                    vm.state.isbnToSearch = it
                },
                onInsertManually = {
                    navigator.navigate(
                        EditBookPageDestination(newBookDestination = BookDestination.WISHLIST)
                    )
                },
                onScanIsbn = {
                    navigator.navigate(ScanIsbnPageDestination(BookDestination.WISHLIST))
                },
                onSearchOnline = {
                    navigator.navigate(SearchBookOnlinePageDestination(BookDestination.WISHLIST))
                },
                modifier = Modifier
                    .focusRequester(fabFocusRequester)
                    .onFocusChanged {
                        if (!it.hasFocus)
                            vm.state.isFabExpanded = false
                    }
                    .focusable()
            )
        }
    }
    LaunchedEffect(vm.selectionManager.singleSelectedItem) {
        vm.selectionManager.singleSelectedItem?.let { item ->
            vm.scaffoldState.bottomSheetContent = {
                RowWithIcon(
                    icon = {
                        Icon(
                            painterResource(R.drawable.place_item_24px),
                            stringResource(R.string.add_to_library)
                        )
                    },
                    onClick = {
                        vm.scaffoldState.setBottomSheetVisibility(false, coroutineScope)
                        vm.moveBookToLibraryAndRefresh(item.info.bookId) {}
                        // vm.state.isContextMenuVisible = false
                    }
                ) {
                    Text(stringResource(R.string.add_to_library))
                }
                RowWithIcon(
                    icon = {
                        Icon(
                            painterResource(R.drawable.info_24px),
                            stringResource(R.string.details)
                        )
                    },
                    onClick = {
                        vm.scaffoldState.setBottomSheetVisibility(false, coroutineScope)
                        navigator.navigate(
                            ViewBookPageDestination(item.info.bookId)
                        )
                        // vm.state.isContextMenuVisible = false
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
                        vm.scaffoldState.setBottomSheetVisibility(false, coroutineScope)
                        navigator.navigate(
                            EditBookPageDestination(item.info.bookId)
                        )
                        // vm.state.isContextMenuVisible = false
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
                            stringResource(R.string.remove_from_wishlist)
                        )
                    },
                    onClick = {
                        vm.scaffoldState.setBottomSheetVisibility(false, coroutineScope)
                        vm.state.showConfirmDeleteBook = true
                        // vm.state.isContextMenuVisible = false
                    }
                ) {
                    Text(
                        stringResource(R.string.remove_from_wishlist)
                    )
                }
            }
        }
    }
    /*
    BottomSheetScaffold(
        sheetPeekHeight = 0.dp,
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {

        }
    ) {*/
    Box(
        modifier = Modifier.nestedScroll(
            vm.scaffoldState.scrollBehavior.nestedScrollConnection
        )
    ) {
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
        LazyColumn(
            state = lazyListState,
        ) {
            items(
                items = lazyPagingItems,
                key = { it.value.info.bookId }
            ) { item ->
                if (item == null)
                    return@items
                Box {
                    WishlistRow(
                        item,
                        onRowTap = {
                            coroutineScope.launch {
                                // vm.scaffoldState.bottomSheetState.hide()
                            }
                            if (vm.selectionManager.isMultipleSelecting) {
                                vm.selectionManager.multipleSelectToggle(item.value)
                            } else {
                                navigator.navigate(
                                    ViewBookPageDestination(item.value.info.bookId)
                                )
                            }
                        },
                        onCoverLongPress = {
                            coroutineScope.launch {
                                // vm.scaffoldState.bottomSheetState.hide()
                            }
                            if (!vm.selectionManager.isMultipleSelecting) {
                                vm.selectionManager.startMultipleSelection(item.value)
                            }
                        },
                        onRowLongPress = {
                            if (!vm.selectionManager.isMultipleSelecting) {
                                vm.selectionManager.singleSelectedItem = item.value
                                Log.d("debug", "Expanding")
                                vm.scaffoldState.setBottomSheetVisibility(true, coroutineScope)
                            }
                        }
                    )
                }
            }
            // TODO manage what happens when added library book is in wishlist
        }
    }
    if (vm.state.showDoubleIsbnDialog) {
        DuplicateIsbnDialog(
            onAddAnyway = {
                importVm.getImportedBooksFromIsbn(
                    vm.duplicateIsbn,
                    maxResults = 2,
                    callback = {
                        vm.state.showDoubleIsbnDialog = false
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
                                    it[0], BookDestination.WISHLIST, vm.translationState
                                ) {
                                    vm.invalidate()
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
                        vm.state.showDoubleIsbnDialog = false
                    },
                )
            },
            onCancel = { vm.state.showDoubleIsbnDialog = false }
        )
    }

    if (vm.state.showConfirmDeleteBook) {
        ConfirmDeleteBookDialog(
            onDismiss = {
                vm.state.showConfirmDeleteBook = false
                if (vm.selectionManager.isMultipleSelecting)
                    vm.selectionManager.clearSelection()
            },
            isPlural = vm.selectionManager.isMultipleSelecting && vm.selectionManager.count > 1
        ) {
            if (vm.selectionManager.isMultipleSelecting)
                vm.deleteSelectedBooksAndRefresh()
            else {
                vm.selectionManager.singleSelectedItem?.bookBundle?.book?.let { book ->
                    vm.deleteBookAndRefresh(book)
                }
            }
            vm.state.showConfirmDeleteBook = false
        }
    }

    disambiguationRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(
                navResult.value, BookDestination.WISHLIST, vm.translationState
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
    TranslationDialog(vm.translationState)
}