package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.PreviewBookDialog
import com.guidofe.pocketlibrary.ui.modules.OnlineBookList
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.SearchBookOnlineVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(route = "search_book_online")
fun SearchBookOnlinePage(
    destination: BookDestination,
    navigator: DestinationsNavigator,
    vm: ISearchBookOnlineVM = hiltViewModel<SearchBookOnlineVM>(),
    importedVm: IImportedBookVM = hiltViewModel<ImportedBookVM>()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isDialogOpen: Boolean by remember { mutableStateOf(false) }
    var selectedBook: ImportedBookData? by remember { mutableStateOf(null) }
    val selectionManager = vm.selectionManager
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(context.getString(R.string.search_online))
    }

    LaunchedEffect(selectionManager.isMultipleSelecting) {
        if (selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
                title = "",
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
                            val selectedItems = vm.selectionManager.selectedItems
                                .value.values.toList()
                            importedVm.checkIfImportedBooksAreAlreadyInLibrary(
                                selectedItems,
                                onAllOk = {
                                    importedVm.saveImportedBooks(selectedItems, destination) {
                                        Snackbars.bookSavedSnackbar(
                                            vm.snackbarHostState,
                                            context,
                                            coroutineScope,
                                            areMultipleBooks = true
                                        ) {}
                                        vm.selectionManager.clearSelection()
                                    }
                                },
                                onConflict = { ok, duplicate ->
                                    importedVm.saveImportedBooks(ok, destination) {}
                                    duplicate.forEach {
                                        Snackbars.bookAlreadyPresentSnackbarWithTitle(
                                            vm.snackbarHostState,
                                            context,
                                            coroutineScope,
                                            it.title,
                                            onDismiss = {}
                                        ) {
                                            importedVm.saveImportedBook(it, destination) {}
                                        }
                                    }
                                    vm.selectionManager.clearSelection()
                                }
                            )
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.save_24px),
                            stringResource(R.string.save)
                        )
                    }
                }
            )
        } else {
            vm.scaffoldState.refreshBar(
                title = context.getString(R.string.search_online)
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            OutlinedTextField(
                value = vm.title,
                onValueChange = { vm.title = it },
                label = { Text(stringResource(R.string.title)) }
            )
            OutlinedTextField(
                value = vm.author,
                onValueChange = { vm.author = it },
                label = { Text(stringResource(R.string.author)) }
            )
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    vm.search()
                    // lazyPagingItems.refresh()
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_24px),
                    contentDescription = stringResource(R.string.search)
                )
            }
            OnlineBookList(
                queryData = vm.queryData,
                multipleSelectionEnabled = true,
                singleTapAction = {
                    selectedBook = it.value
                    isDialogOpen = true
                },
                selectionManager = vm.selectionManager,
                vm = vm.listVM,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    if (isDialogOpen) {
        PreviewBookDialog(
            bookData = selectedBook,
            onSaveButtonClicked = {
                selectedBook?.let { importedBook ->
                    vm.saveBook(importedBook, destination) {
                        if (destination == BookDestination.LIBRARY)
                            navigator.navigate(ViewBookPageDestination(bookId = it))
                        else
                            vm.selectionManager.clearSelection()
                    }
                }
            },
            onDismissRequest = { isDialogOpen = false }
        )
    }
}