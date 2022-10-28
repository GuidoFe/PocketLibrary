package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.modules.CustomSnackbarVisuals
import com.guidofe.pocketlibrary.ui.modules.OnlineBookList
import com.guidofe.pocketlibrary.ui.pages.librarypage.PreviewBookDialog
import com.guidofe.pocketlibrary.viewmodels.SearchBookOnlineVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ISearchBookOnlineVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Destination(route = "search_book_online")
fun SearchBookOnlinePage(
    navigator: DestinationsNavigator,
    vm: ISearchBookOnlineVM = hiltViewModel<SearchBookOnlineVM>(),
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isDialogOpen: Boolean by remember{mutableStateOf(false)}
    var selectedBook: ImportedBookData? by remember{mutableStateOf(null)}
    val isMutableSelecting by vm.selectionManager.isMutableSelecting.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(context.getString(R.string.search_online))
    }

    LaunchedEffect(isMutableSelecting) {
        if (isMutableSelecting) {
            vm.scaffoldState.refreshBar (
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
                            vm.saveSelectedBooks {
                                vm.selectionManager.clearSelection()
                                coroutineScope.launch {
                                    vm.snackbarHostState.showSnackbar(
                                        CustomSnackbarVisuals(
                                            message = context.getString(R.string.books_saved)
                                        )
                                    )
                                }
                            }
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
                    //lazyPagingItems.refresh()
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
    if(isDialogOpen) {
        PreviewBookDialog(
            bookData = selectedBook,
            onSaveButtonClicked = {
                selectedBook?.let { importedBook ->
                    vm.saveBook(importedBook) {
                        navigator.navigate(ViewBookPageDestination(bookId = it))
                    }
                }
            },
            onDismissRequest = {isDialogOpen = false}
        )
    }
}