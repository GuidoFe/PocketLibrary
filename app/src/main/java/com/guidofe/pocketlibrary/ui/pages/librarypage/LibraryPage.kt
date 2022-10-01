package com.guidofe.pocketlibrary.ui.pages.librarypage

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.AppBarState
import com.guidofe.pocketlibrary.ui.modules.LibraryListItem
import com.guidofe.pocketlibrary.viewmodels.ILibraryViewModel
import com.guidofe.pocketlibrary.viewmodels.LibraryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun LibraryPage(
    navigator: DestinationsNavigator,
    viewModel: ILibraryViewModel = hiltViewModel<LibraryViewModel>(),
) {
    val lazyPagingItems = viewModel.pager.collectAsLazyPagingItems()
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.appBarDelegate.setAppBarContent(
            AppBarState(title=context.getString(R.string.library)
            )
        )
    }
    LazyColumn {
        items(
            items = lazyPagingItems,
            key = {bundle -> bundle.book.bookId}
        ) { bundle ->
            if(bundle != null)
                LibraryListItem(bundle)
            //TODO: add placeholder if bundle != null
        }
    }
}