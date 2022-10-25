package com.guidofe.pocketlibrary.ui.modules

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.MultipleSelectionManager
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.viewmodels.OnlineBookListVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM

@Composable
fun OnlineBookList(
    queryData: QueryData?,
    modifier: Modifier = Modifier,
    singleTapAction: (SelectableListItem<ImportedBookData>) -> Unit = {},
    longPressAction: (SelectableListItem<ImportedBookData>) -> Unit = {},
    selectionManager: MultipleSelectionManager<String, ImportedBookData> = MultipleSelectionManager(
        getKey = {it.externalId}
    ),
    multipleSelectionEnabled: Boolean = true,
    vm: IOnlineBookListVM = hiltViewModel<OnlineBookListVM>()
) {
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()
    val isMutableSelecting by selectionManager.isMutableSelecting.collectAsState()

    LaunchedEffect(queryData) {
        vm.query = queryData
        lazyPagingItems.refresh()
    }

    Box(modifier = modifier) {
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is LoadState.Error -> {
                Text(
                    stringResource(R.string.network_error_sadface),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn {
                    if (lazyPagingItems.loadState.prepend == LoadState.Loading) {
                        item {
                            Text(stringResource(R.string.loading_dots))
                        }
                    }
                    items(
                        items = lazyPagingItems,
                    ) { i ->
                        i?.let { item ->
                            ImportedBookListRow(
                                item,
                                onRowTap = {
                                    if (isMutableSelecting) {
                                        Log.d("debug", "Selecting")
                                        selectionManager.multipleSelectToggle(item.value)
                                    }
                                    else
                                        singleTapAction(item)
                                },
                                onRowLongPress = {
                                    if (!isMutableSelecting)
                                        longPressAction(item)
                                },
                                onCoverLongPress = {
                                    if (multipleSelectionEnabled) {
                                        if (!isMutableSelecting) {
                                            selectionManager.startMultipleSelection(item.value)
                                        }
                                    } else {
                                        longPressAction(item)
                                    }
                                },
                            )
                        }
                    }
                    if (lazyPagingItems.loadState.append == LoadState.Loading) {
                        item {
                            Text(stringResource(R.string.loading_dots))
                        }
                    }
                }
            }
        }
    }
}