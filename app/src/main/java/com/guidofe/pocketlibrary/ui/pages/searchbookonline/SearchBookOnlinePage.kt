package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.PreviewBookDialog
import com.guidofe.pocketlibrary.ui.dialogs.TranslationDialog
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.OnlineBookList
import com.guidofe.pocketlibrary.ui.modules.Snackbars
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.searchbookonline.SearchBox
import com.guidofe.pocketlibrary.ui.utils.appBarColorAnimation
import com.guidofe.pocketlibrary.utils.BookDestination
import com.guidofe.pocketlibrary.viewmodels.ImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.SearchBookOnlineVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import com.guidofe.pocketlibrary.viewmodels.previews.ImportedBookVMPreview
import com.guidofe.pocketlibrary.viewmodels.previews.SearchBookOnlineVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    val boxShape = ShapeDefaults.Large
    val appBarColor by appBarColorAnimation(vm.scaffoldState.scrollBehavior)
    var isSearchBoxVisible by remember { mutableStateOf(false) }
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar({ Text(stringResource(R.string.search_online)) })
    }

    LaunchedEffect(selectionManager.isMultipleSelecting) {
        if (selectionManager.isMultipleSelecting) {
            vm.scaffoldState.refreshBar(
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
                                    importVm.saveImportedBooks(
                                        selectedItems, destination, vm.translationState
                                    ) {
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
                                    importVm.saveImportedBooks(
                                        ok, destination, vm.translationState
                                    ) {}
                                    duplicate.forEach {
                                        Snackbars.bookAlreadyPresentSnackbarWithTitle(
                                            vm.snackbarHostState,
                                            context,
                                            coroutineScope,
                                            it.title,
                                            onDismiss = {}
                                        ) {
                                            importVm.saveImportedBook(
                                                it, destination, vm.translationState
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
                title = { Text(stringResource(R.string.search_online)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(
                            painterResource(R.drawable.arrow_back_24px),
                            stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        visible = !isSearchBoxVisible
                    ) {
                        IconButton(
                            onClick = { isSearchBoxVisible = true }
                        ) {
                            Icon(
                                painterResource(R.drawable.search_24px),
                                stringResource(R.string.open_search_box)
                            )
                        }
                    }
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(vm.scaffoldState.scrollBehavior.nestedScrollConnection)
    ) {
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
            onInitialEmptyList = { isSearchBoxVisible = true },
            modifier = Modifier.fillMaxSize()
        )
    }
    if (isDialogOpen) {
        PreviewBookDialog(
            bookData = selectedBook,
            onSaveButtonClicked = {
                isDialogOpen = false
                selectedBook?.let { importedBook ->
                    importVm.saveImportedBook(importedBook, destination, vm.translationState) {
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
            onDismissRequest = { isDialogOpen = false },
        )
    }
    if (isSearchBoxVisible) {
        Dialog(
            onDismissRequest = { isSearchBoxVisible = false }
        ) {
            SearchBox(
                title = vm.title,
                author = vm.author,
                lang = vm.langField,
                setTitle = { vm.title = it },
                setAuthor = { vm.author = it },
                setLang = { vm.langField = it },
                onStartSearch = {
                    focusManager.clearFocus()
                    vm.search()
                    isSearchBoxVisible = false
                },
                onEmptyFieldsError = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        vm.snackbarHostState.showSnackbar(
                            CustomSnackbarVisuals(
                                message = context.getString(R.string.please_insert_serch_term)
                            )
                        )
                    }
                },
                onCancel = {
                    navigator.navigateUp()
                },
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxWidth()
                    .shadow(3.dp, shape = boxShape)
                    .background(appBarColor, boxShape)
                    .padding(16.dp)
            )
        }
    }
    TranslationDialog(vm.translationState)
}

@Preview(showSystemUi = true, device = Devices.PHONE)
@Preview(showSystemUi = true, device = Devices.TABLET)
@Composable
private fun SearchBoxOnlinePagePreview() {
    SearchBookOnlinePage(
        BookDestination.LIBRARY,
        EmptyDestinationsNavigator,
        SearchBookOnlineVMPreview(),
        ImportedBookVMPreview()
    )
}