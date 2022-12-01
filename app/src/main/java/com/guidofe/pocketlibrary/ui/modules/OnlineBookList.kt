package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.remote.google_book.QueryData
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.SelectionManager
import com.guidofe.pocketlibrary.viewmodels.OnlineBookListVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IOnlineBookListVM

@Composable
fun OnlineBookList(
    queryData: QueryData?,
    modifier: Modifier = Modifier,
    langRestrict: String? = null,
    singleTapAction: (SelectableListItem<ImportedBookData>) -> Unit = {},
    longPressAction: (SelectableListItem<ImportedBookData>) -> Unit = {},
    selectionManager: SelectionManager<String, ImportedBookData> = SelectionManager(
        getKey = { it.externalId }
    ),
    multipleSelectionEnabled: Boolean = true,
    nestedScrollConnection: NestedScrollConnection,
    vm: IOnlineBookListVM = hiltViewModel<OnlineBookListVM>()
) {
    val lazyPagingItems = vm.pager.collectAsLazyPagingItems()

    LaunchedEffect(queryData, langRestrict) {
        vm.query = queryData
        vm.langRestrict = langRestrict
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
                LazyColumn(
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                ) {
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
                                    if (selectionManager.isMultipleSelecting) {
                                        selectionManager.multipleSelectToggle(item.value)
                                    } else
                                        singleTapAction(item)
                                },
                                onRowLongPress = {
                                    if (!selectionManager.isMultipleSelecting)
                                        longPressAction(item)
                                },
                                onCoverLongPress = {
                                    if (multipleSelectionEnabled) {
                                        if (!selectionManager.isMultipleSelecting) {
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