package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.dialogs.PreviewBookDialog
import com.guidofe.pocketlibrary.ui.modules.ImportedBookListRow
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.BookDisambiguationVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookDisambiguationVM
import com.guidofe.pocketlibrary.viewmodels.previews.BookDisambiguationVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.EmptyResultBackNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun BookDisambiguationPage(
    navigator: ResultBackNavigator<ImportedBookData>,
    bookList: Array<ImportedBookData>,
    vm: IBookDisambiguationVM = hiltViewModel<BookDisambiguationVM>()
) {
    var isDialogOpen: Boolean by remember { mutableStateOf(false) }
    var selectedBook: ImportedBookData? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.choose_book)) },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigateBack()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
    }
    LazyColumn(
        modifier = Modifier
        // .nestedScroll(vm.scaffoldState.scrollBehavior!!.nestedScrollConnection)
    ) {
        items(
            bookList.map { SelectableListItem(it, false) },
            { it.value.externalId }
        ) { item ->
            ImportedBookListRow(
                item = item,
                onRowTap = {
                    selectedBook = item.value
                    isDialogOpen = true
                }
            )
        }
    }
    if (isDialogOpen) {
        PreviewBookDialog(
            bookData = selectedBook,
            onSaveButtonClicked = {
                selectedBook?.let { importedBook ->
                    navigator.navigateBack(importedBook)
                }
            },
            onDismissRequest = { isDialogOpen = false }
        )
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun PreviewBookDisambiguationPage() {
    MaterialTheme {
        BookDisambiguationPage(
            navigator = EmptyResultBackNavigator(),
            bookList = arrayOf(PreviewUtils.exampleImportedBook),
            vm = BookDisambiguationVMPreview()
        )
    }
}