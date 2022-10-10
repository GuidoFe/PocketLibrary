package com.guidofe.pocketlibrary.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.ImportedBookData
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.viewmodels.ChooseImportedBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IChooseImportedBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

private data class IndexedBook(val index: Int, val data: ImportedBookData)

@Composable
private fun CardContent(data: ImportedBookData) {
    if (data.coverUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(data.coverUrl)
                .build(),
            contentDescription = stringResource(id = R.string.cover),
            modifier = Modifier.fillMaxHeight(0.3f)
        )
    } else {
        Image(
            //TODO: change large cover placeholder
            painterResource(id = R.drawable.large_cover),
            stringResource(R.string.cover),
            modifier = Modifier.fillMaxHeight(0.3f)
        )
    }
    Text(data.title)
    Text(data.authors.joinToString(", "))
}

@OptIn(ExperimentalSnapperApi::class, ExperimentalMaterial3Api::class)
@Composable
@Destination
fun ChooseImportedBookPage(
    navigator: DestinationsNavigator,
    list: Array<ImportedBookData>,
    vm: IChooseImportedBookVM = hiltViewModel<ChooseImportedBookVM>()
) {
    val lazyListState = rememberLazyListState()
    var selected: Int? by remember{mutableStateOf(null)}
    val indexedList = list.mapIndexed { index, importedBookData ->
        IndexedValue<ImportedBookData>(index, importedBookData)
    }
    LaunchedEffect(selected) {
        if(selected != null) {
            vm.appBarDelegate.setActions {
                IconButton(
                    onClick = {
                        vm.saveImportedBook(list[selected!!]) {
                            if (it > 0)
                                navigator.navigate(ViewBookPageDestination(it))
                            //TODO: Manage else branch
                        }

                    }
                ) {
                    Icon(painterResource(
                        R.drawable.check_24px),
                        stringResource(R.string.select_current_book
                    ))
                }
            }
        }
    }
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val cardModifier = Modifier
            .width(this@BoxWithConstraints.maxWidth * 0.8f)
            .height(this@BoxWithConstraints.maxWidth * 1.4f)
        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState),
            horizontalArrangement = Arrangement
                .spacedBy(this@BoxWithConstraints.maxWidth * 0.1f)
        ) {
            items(indexedList) { element ->
                val data = element.value
                if (selected == element.index) {
                    OutlinedCard(onClick = {selected = element.index}, modifier = cardModifier) {
                        CardContent(data)
                    }
                } else {
                    Card(onClick = {selected = element.index}, modifier = cardModifier) {
                        CardContent(data)
                    }
                }
            }
        }
    }

}