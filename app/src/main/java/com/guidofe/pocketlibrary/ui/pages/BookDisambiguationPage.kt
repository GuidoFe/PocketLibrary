package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.modules.OnlineBookList
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.librarypage.PreviewBookDialog
import com.guidofe.pocketlibrary.viewmodels.BasicPageVM
import com.guidofe.pocketlibrary.viewmodels.BookDisambiguationVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBasicPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IBookDisambiguationVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.chrisbanes.snapper.ExperimentalSnapperApi


@OptIn(ExperimentalSnapperApi::class, ExperimentalMaterial3Api::class)
@Composable
@Destination
fun BookDisambiguationPage(
    navigator: DestinationsNavigator,
    isbn: String,
    vm: IBookDisambiguationVM = hiltViewModel<BookDisambiguationVM>()
) {
    var isDialogOpen: Boolean by remember{ mutableStateOf(false) }
    var selectedBook: ImportedBookData? by remember{mutableStateOf(null)}
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.title = context.getString(R.string.choose_book)
    }
    OnlineBookList(
        queryData = QueryData(null, mapOf(QueryData.QueryKey.isbn to isbn)),
        multipleSelectionEnabled = false,
        singleTapAction = {
            vm.saveBook(it.value) { id ->
                if (id > 0)
                    navigator.navigate(ViewBookPageDestination(id))
            }
        }
    )
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


@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun PreviewBookDisambiguationPage() {
    MaterialTheme{
        BookDisambiguationPage(
            navigator = EmptyDestinationsNavigator,
            isbn = "4325",
            vm = object: IBookDisambiguationVM {
                override val scaffoldState = ScaffoldState()
                override val snackbarHostState = SnackbarHostState()
                override fun saveBook(importedBook: ImportedBookData, callback: (Long) -> Unit) {}
            }
        )
    }
}