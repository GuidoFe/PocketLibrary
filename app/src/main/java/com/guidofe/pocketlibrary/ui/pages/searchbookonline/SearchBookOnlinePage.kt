package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
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
    val density = LocalDensity.current
    val boxShape = ShapeDefaults.Large.copy(topStart = ZeroCornerSize, topEnd = ZeroCornerSize)
    val appBarColor by appBarColorAnimation(vm.scaffoldState.scrollBehavior)
    var searchBoxYOffset by remember { mutableStateOf(0f) }
    var isSearchBoxVisible by remember { mutableStateOf(true) }
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
    Box(modifier = Modifier.fillMaxSize()) {
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
        AnimatedVisibility(
            isSearchBoxVisible,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
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
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, shape = boxShape)
                    .background(appBarColor, boxShape)
                    .padding(8.dp)
            )
        }
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
            modifier = Modifier.background(appBarColor)
        )
    }
    TranslationDialog(vm.translationState)
}