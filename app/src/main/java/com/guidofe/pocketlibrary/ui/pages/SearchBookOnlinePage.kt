package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.PreviewBookDialog
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialog
import com.guidofe.pocketlibrary.ui.modules.LanguageAutocomplete
import com.guidofe.pocketlibrary.ui.modules.OnlineBookList
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.utils.appBarColorAnimation
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.utils.TranslationPhase
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.SearchBookOnlineVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(route = "search_book_online")
fun SearchBookOnlinePage(
    destination: BookDestination,
    navigator: DestinationsNavigator,
    vm: ISearchBookOnlineVM = hiltViewModel<SearchBookOnlineVM>(),
    importVm: IImportedBookVM = hiltViewModel<ImportedBookVM>()
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isDialogOpen: Boolean by remember { mutableStateOf(false) }
    var selectedBook: ImportedBookData? by remember { mutableStateOf(null) }
    val selectionManager = vm.selectionManager
    val coroutineScope = rememberCoroutineScope()
    val appBarColor by appBarColorAnimation(vm.scaffoldState.scrollBehavior)
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
                            painterResource(R.drawable.backspace_24px),
                            stringResource(R.string.clear_selection)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val selectedItems = vm.selectionManager.selectedItems
                                .value.values.toList()
                            importVm.checkIfImportedBooksAreAlreadyInLibrary(
                                selectedItems,
                                onAllOk = {
                                    importVm.saveImportedBooks(selectedItems, destination) {
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
                                    importVm.saveImportedBooks(ok, destination) {}
                                    duplicate.forEach {
                                        Snackbars.bookAlreadyPresentSnackbarWithTitle(
                                            vm.snackbarHostState,
                                            context,
                                            coroutineScope,
                                            it.title,
                                            onDismiss = {}
                                        ) {
                                            importVm.saveImportedBook(
                                                it, destination
                                            ) {}
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
                title = context.getString(R.string.search_online),
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(
                            painterResource(R.drawable.arrow_back_24px),
                            stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val interSpace = 5.dp
        Column(
            verticalArrangement = Arrangement.spacedBy(interSpace)
        ) {
            val searchPadding = 10.dp
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(appBarColor)
                    .padding(searchPadding, searchPadding, searchPadding, 0.dp)
            ) {
                OutlinedTextField(
                    value = vm.title,
                    onValueChange = {
                        vm.title = it
                    },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(interSpace)
                ) {
                    OutlinedTextField(
                        value = vm.author,
                        onValueChange = { vm.author = it },
                        label = { Text(stringResource(R.string.author)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    LanguageAutocomplete(
                        text = vm.langField,
                        onTextChange = { vm.langField = it },
                        label = { Text(stringResource(R.string.language)) },
                        onOptionSelected = { vm.langField = it },
                        modifier = Modifier.weight(1f)
                    )
                }
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
            }
            OnlineBookList(
                queryData = vm.queryData,
                langRestrict = vm.langRestrict,
                multipleSelectionEnabled = true,
                singleTapAction = {
                    selectedBook = it.value
                    isDialogOpen = true
                },
                selectionManager = vm.selectionManager,
                vm = vm.listVM,
                nestedScrollConnection = vm.scaffoldState.scrollBehavior!!.nestedScrollConnection,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    if (isDialogOpen) {
        PreviewBookDialog(
            bookData = selectedBook,
            onSaveButtonClicked = {
                isDialogOpen = false
                selectedBook?.let { importedBook ->
                    importVm.saveImportedBook(importedBook, destination) {
                        // TODO: Manage error
                        if (it < 0) return@saveImportedBook
                        coroutineScope.launch(Dispatchers.Main) {
                            if (destination == BookDestination.LIBRARY)
                                navigator.navigate(ViewBookPageDestination(bookId = it))
                            else
                                vm.selectionManager.clearSelection()
                        }
                    }
                }
            },
            onDismissRequest = { isDialogOpen = false }
        )
    }
    if (importVm.translationDialogState.translationPhase != TranslationPhase.NO_TRANSLATING) {
        TranslationDialog(importVm.translationDialogState)
    }
}