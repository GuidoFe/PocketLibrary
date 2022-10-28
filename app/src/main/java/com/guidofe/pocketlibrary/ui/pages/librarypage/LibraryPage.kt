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
import com.guidofe.pocketlibrary.ui.destinations.BookDisambiguationPageDestination
import com.guidofe.pocketlibrary.ui.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.modules.AddBookFab
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.DuplicateIsbnDialog
import com.guidofe.pocketlibrary.ui.modules.LibraryListRow
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryVM
import com.guidofe.pocketlibrary.viewmodels.LibraryVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import kotlinx.coroutines.launch
import androidx.paging.compose.items

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun LibraryPage(
    navigator: DestinationsNavigator,
    vm: ILibraryVM = hiltViewModel<LibraryVM>(),
    importedBookVm: IImportedBookVM = hiltViewModel<ImportedBookVM>()
) {
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()
    val context = LocalContext.current
    var isExpanded: Boolean by remember{mutableStateOf(false)}
    val scope = rememberCoroutineScope()
    val isMultipleSelecting by vm.selectionManager.isMutableSelecting.collectAsState()
    var isStarButtonFilled by remember{mutableStateOf(false)}
    var showDoubleIsbnDialog by remember{mutableStateOf(false)}
    LaunchedEffect(isMultipleSelecting) {
        if (isMultipleSelecting) {
            isStarButtonFilled = false
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
                            isStarButtonFilled = !isStarButtonFilled
                            vm.setFavorite(
                                vm.selectionManager.selectedKeys,
                                isStarButtonFilled
                            )
                        }
                    ) {
                        if(isStarButtonFilled)
                            Icon(
                                painterResource(R.drawable.star_filled_24px),
                                stringResource(R.string.remove_from_favorites)
                            )
                        else
                            Icon(
                                painterResource(R.drawable.star_24px),
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
    LaunchedEffect(key1 = navigator) {
        vm.scaffoldState.fab = {
            AddBookFab(
                navigator = navigator,
                isExpanded = isExpanded,
                onMainFabClick = { isExpanded = !isExpanded },
                onDismissRequest = { isExpanded = false},
                onSearchByIsbn = { isbn ->
                    importedBookVm.getImportedBooksFromIsbn(
                        isbn,
                        maxResults = 2,
                        callback = {
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
                                    importedBookVm.saveImportedBookInDb(it[0]) { id ->
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
                        },
                    )
                },
                insertIsbnRecipient = EmptyResultRecipient(),
                scanIsbnRecipient = EmptyResultRecipient()

            )
        }
    }
    LazyColumn {
        items(
            items = lazyPagingItems,
            key = {it.value.book.bookId}
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
                        navigator.navigate(ViewBookPageDestination(item.value.book.bookId))
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
                importedBookVm.getImportedBooksFromIsbn(
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
                                importedBookVm.saveImportedBookInDb(it[0]) { id ->
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
}