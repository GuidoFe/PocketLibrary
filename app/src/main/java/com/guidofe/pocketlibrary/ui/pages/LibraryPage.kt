package com.guidofe.pocketlibrary.ui.pages.librarypage

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.guidofe.pocketlibrary.ui.modules.*
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.guidofe.pocketlibrary.utils.BookDestination
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

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
    val isMultipleSelecting by vm.selectionManager.isMutableSelecting.collectAsState()
    var isFavoriteButtonFilled by remember{mutableStateOf(false)}
    var showDoubleIsbnDialog by remember{mutableStateOf(false)}
    var isbnToSearch: String? by remember{mutableStateOf(null)}
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
                            vm.deleteSelectedBooks()
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            stringResource(R.string.delete)
                        //TODO Ask For Confirm
                        )
                    }
                    IconButton(
                        onClick = {
                            isFavoriteButtonFilled = !isFavoriteButtonFilled
                            vm.setFavorite(
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
                        navigator.navigate(EditBookPageDestination())
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
                    navigator.navigate(EditBookPageDestination())
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
            key = {it.value.libraryInfo.bookId}
        ) { item ->
            if (item == null)
                return@items
            LibraryListRow(
                item,
                onRowTap = {
                    if (isMultipleSelecting) {
                        vm.selectionManager.multipleSelectToggle(item.value)
                    }
                    else
                        navigator.navigate(ViewBookPageDestination(item.value.libraryInfo.bookId))
                },
                onCoverLongPress = {
                    if (!isMultipleSelecting) {
                        vm.selectionManager.startMultipleSelection(item.value)
                    }
                },
            )
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