@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.guidofe.pocketlibrary.ui.pages

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.modules.FAB
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.editbookpage.EditBookPageNavArgs
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.DetailsTab
import com.guidofe.pocketlibrary.ui.pages.viewbookpage.LocationTab
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.utils.AppBarStateDelegate
import com.guidofe.pocketlibrary.viewmodels.ILocationViewModel
import com.guidofe.pocketlibrary.viewmodels.IViewBookViewModel
import com.guidofe.pocketlibrary.viewmodels.LocationViewModel
import com.guidofe.pocketlibrary.viewmodels.ViewBookViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Destination
@Composable
fun ViewBookPage (
    bookId: Long,
    vm: IViewBookViewModel = hiltViewModel<ViewBookViewModel>(),
    locationVm: ILocationViewModel = hiltViewModel<LocationViewModel>(),
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        vm.appBarDelegate.setTitle(context.getString(R.string.book_details))
    }
    LaunchedEffect(key1 = bookId) {
        vm.initBundle(bookId)
    }
    val bundle by vm.bundle.collectAsState()
    ViewBookContent(bundle?: BookBundle.EMPTY, vm, locationVm, navigator)
}

private enum class LocalTab {SUMMARY, DETAILS, NOTE, LOCATION}
//IsOwned, isFavorite, progress
@Composable
private fun ViewBookContent(
    bundle: BookBundle,
    vm: IViewBookViewModel,
    locationVm: ILocationViewModel,
    navigator: DestinationsNavigator,
    defaultTab: Int = 0,
) {
    var tabState by remember { mutableStateOf(LocalTab.SUMMARY) }
    val book = bundle.book
    val detailsScrollState = rememberScrollState()
    val genreScrollState = rememberScrollState()
    val editedNote = vm.editedNoteFlow.collectAsState()
    var notePosition by remember { mutableStateOf(0f)}
    var  hasNoteBeenModified by remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current
    LaunchedEffect(book.bookId) {
        bundle.place?.let { place ->
            locationVm.setValues(
                place.name,
                (bundle.room?.name) ?: "",
                (bundle.bookshelf?.name) ?: ""
            )
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {

        Surface() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        Log.d("debug", "Clearing focus")
                        localFocusManager.clearFocus()
                    })
                }
            ) {
                val coverURI = book.coverURI
                if (coverURI != null) {
                    //TODO: placeholder for book cover
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverURI)
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
                Text(book.title, style = MaterialTheme.typography.titleLarge)
                book.subtitle?.let { Text(it, style = MaterialTheme.typography.titleSmall) }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.horizontalScroll(genreScrollState)
                ) {
                    bundle.genres.forEach {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(it.name) },
                        )
                    }
                }
                ScrollableTabRow(
                    selectedTabIndex = tabState.ordinal,
                    edgePadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = tabState == LocalTab.SUMMARY,
                        onClick = { tabState = LocalTab.SUMMARY },
                        text = { Text(stringResource(R.string.summary)) }
                    )
                    Tab(
                        selected = tabState == LocalTab.DETAILS,
                        onClick = { tabState = LocalTab.DETAILS },
                        text = { Text(stringResource(R.string.details)) }
                    )
                    Tab(
                        selected = tabState == LocalTab.NOTE,
                        onClick = { tabState = LocalTab.NOTE },
                        text = { Text(stringResource(R.string.note)) }
                    )
                    Tab(
                        selected = tabState == LocalTab.LOCATION,
                        onClick = { tabState = LocalTab.LOCATION },
                        text = { Text(stringResource(R.string.location)) }
                    )
                }
                Box(
                    Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    when (tabState) {
                        LocalTab.SUMMARY -> {
                            if (book.description.isNullOrBlank()) {
                                Text(
                                    stringResource(R.string.no_description),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                Text(
                                    book.description ?: stringResource(R.string.no_description),
                                )
                            }
                        }
                        LocalTab.DETAILS -> {
                            DetailsTab(book, modifier = Modifier.verticalScroll(detailsScrollState))
                        }
                        LocalTab.NOTE -> {
                            OutlinedTextField(
                                value = editedNote.value,
                                placeholder = { Text(stringResource(R.string.note_placeholder)) },

                                onValueChange = {
                                    vm.editedNoteFlow.value = it
                                    hasNoteBeenModified = true
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                            )

                        }
                        LocalTab.LOCATION -> {
                            Surface() {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    LocationTab(bookId = book.bookId, locationVm)
                                }
                            }
                        }
                    }
                }
            }
        }
        val fabModifier = Modifier
            .align(Alignment.BottomEnd)
            .offset((-16).dp, (-16).dp)
        val iconModifier = Modifier
        when (tabState) {
            LocalTab.DETAILS, LocalTab.SUMMARY -> {
                FAB(
                    onClick = { navigator.navigate(EditBookPageDestination(book.bookId))},
                    modifier = fabModifier
                ) {
                    Icon(
                        painterResource(R.drawable.edit_48px),
                        stringResource(R.string.edit_details),
                        modifier = iconModifier
                    )
                }
            }
            LocalTab.NOTE -> {
                if (hasNoteBeenModified) {
                    FAB(
                        onClick = {
                            vm.saveNote()
                            hasNoteBeenModified = false
                        },
                        modifier = fabModifier
                        ) {
                        Icon(
                            painterResource(R.drawable.save_48px),
                            stringResource(R.string.save),
                        )
                    }
                }
            }
            LocalTab.LOCATION -> {
                if (locationVm.hasLocationBeenModified) {
                    FAB(
                        onClick = {
                            locationVm.saveLocation(book.bookId)
                            locationVm.hasLocationBeenModified = false
                        },
                        modifier = fabModifier
                    ) {
                        Icon(
                            painterResource(R.drawable.save_48px),
                            stringResource(R.string.save),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_2, showSystemUi = true)
fun ViewBookPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        ViewBookContent(
            PreviewUtils.exampleBookBundle,
            object: IViewBookViewModel {
                override var bundle: StateFlow<BookBundle?> =
                    MutableStateFlow(PreviewUtils.exampleBookBundle)
                override var editedNoteFlow = MutableStateFlow("")
                override fun initBundle(bookId: Long) {}
                override fun saveNote() {}
                override val appBarDelegate: AppBarStateDelegate = AppBarStateDelegate(MutableStateFlow(null))
            },
            object: ILocationViewModel {
                override var placeText: String = "Place"
                override var roomText: String = "Room"
                override var bookshelfText: String = "Bookshelf"
                override val places: StateFlow<List<String>> = MutableStateFlow(listOf())
                override val possibleRooms: StateFlow<List<String>> = MutableStateFlow(listOf())
                override val possibleBookshelves: StateFlow<List<String>> = MutableStateFlow(listOf())
                override var hasLocationBeenModified: Boolean = true
                override fun setValues(place: String, room: String, bookshelf: String) { }
                override fun changedPlace() { }
                override fun changedRoom() {}
                override fun saveLocation(bookId: Long) { }
            },
            EmptyDestinationsNavigator
        )
    }
}