package com.guidofe.pocketlibrary.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.WishlistPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IWishlistPageVM
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

//TODO: Undo delete action

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
    var isExpanded: Boolean by remember{mutableStateOf(false)}
    val scope = rememberCoroutineScope()
    val isMultipleSelecting by vm.selectionManager.isMutableSelecting.collectAsState()
    var showDoubleIsbnDialog by remember{mutableStateOf(false)}
    var isbnToSearch: String? by remember{mutableStateOf(null)}
    var showConfirmDeleteBook by remember{mutableStateOf(false)}

    LaunchedEffect(isMultipleSelecting) {
        if (isMultipleSelecting) {
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
                        //TODO Ask For Confirm
                        )
                    }
                }
            )
        } else {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.wishlist)
            )
        }
    }
    LaunchedEffect(isbnToSearch) {
        isbnToSearch?.let {
            importVm.getAndSaveBookFromIsbnFlow(
                it,
                BookDestination.WISHLIST,
                onNetworkError = {
                    Snackbars.connectionErrorSnackbar(importVm.snackbarHostState, context, scope)
                },
                onNoBookFound = {
                    Snackbars.noBookFoundForIsbnSnackbar(importVm.snackbarHostState, context, scope) {
                        navigator.navigate(
                            EditBookPageDestination(newBookDestination = BookDestination.WISHLIST)
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
                        EditBookPageDestination(newBookDestination = BookDestination.WISHLIST)
                    )
                },
                onScanIsbn = {
                    navigator.navigate(ScanIsbnPageDestination(BookDestination.WISHLIST))
                },
                onSearchOnline = {
                    navigator.navigate(SearchBookOnlinePageDestination(BookDestination.WISHLIST))
                }
            )
        }
    }
    LazyColumn {
        items(
            items = lazyPagingItems,
            key = {it.value.wishlist.bookId}
        ) { item ->
            if (item == null)
                return@items
            Box {
                var itemDropdownOpen by remember { mutableStateOf(false) }
                WishlistRow(
                    item,
                    onRowTap = {
                        if (isMultipleSelecting) {
                            vm.selectionManager.multipleSelectToggle(item.value)
                        }
                    },
                    onCoverLongPress = {
                        if (!isMultipleSelecting) {
                            vm.selectionManager.startMultipleSelection(item.value)
                        }
                    },
                    onRowLongPress = {
                        if (isMultipleSelecting) return@WishlistRow
                        itemDropdownOpen = true

                    }
                )
                DropdownMenu(
                    expanded = itemDropdownOpen,
                    onDismissRequest = { itemDropdownOpen = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.add_to_library)) },
                        onClick = {vm.moveBookToLibraryAndRefresh(item.value.wishlist.bookId)}
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.edit_details)) },
                        onClick = {
                            navigator.navigate(EditBookPageDestination(item.value.wishlist.bookId))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.remove_from_wishlist)) },
                        onClick = {
                            vm.selectedBook = item.value.bookBundle.book
                            showConfirmDeleteBook = true
                        }
                    )
                }
            }

        }
        //TODO manage what happens when added library book is in wishlist
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
                                importVm.saveImportedBook(it[0], BookDestination.WISHLIST) { id ->
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
            isPlural = isMultipleSelecting && vm.selectionManager.selectedKeys.size > 1
        ) {
            if(isMultipleSelecting)
                vm.deleteSelectedBooksAndRefresh()
            else
                vm.deleteSelectedBookAndRefresh()
            showConfirmDeleteBook = false
        }
    }

    disambiguationRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            importVm.saveImportedBook(navResult.value, BookDestination.WISHLIST) {
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