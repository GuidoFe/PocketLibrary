@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.modules.ScaffoldState
import com.guidofe.pocketlibrary.ui.modules.FAB
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import com.guidofe.pocketlibrary.viewmodels.ViewBookVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

private enum class LocalTab {SUMMARY, DETAILS, NOTE}
private enum class DisplayedFab {NONE, EDIT_BOOK, SAVE_NOTE}
@Destination
@Composable
fun ViewBookPage (
    bookId: Long? = null,
    vm: IViewBookVM = hiltViewModel<ViewBookVM>(),
    navigator: DestinationsNavigator,
    defaultTab: Int = 0,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(context.getString(R.string.book_details))
        vm.scaffoldState.fab = {}
    }
    LaunchedEffect(key1 = bookId) {
        if (bookId != null)
            vm.initFromLibraryBook(bookId)
    }

    var tabState by remember { mutableStateOf(LocalTab.SUMMARY) }
    var localFabState by remember {mutableStateOf(DisplayedFab.NONE)}
    val detailsScrollState = rememberScrollState()
    val summaryScrollState = rememberScrollState()
    val genreScrollState = rememberScrollState()
    var notePosition by remember { mutableStateOf(0f)}
    var hasNoteBeenModified by remember { mutableStateOf(false) }
    val localFocusManager = LocalFocusManager.current

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val boxScope = this
        Surface() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = min(300.dp, boxScope.maxHeight / 2 - 50.dp))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val coverURI = vm.data?.coverURI
                    if (coverURI != null) {
                        //TODO: placeholder for book cover
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(coverURI)
                                .build(),
                            contentDescription = stringResource(id = R.string.cover),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.67f)
                                .weight(1f)
                                .border(5.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                        ) {
                            Text(
                                stringResource(R.string.no_cover_sadface),
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                softWrap = true,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    Box(modifier = Modifier) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                vm.data?.title ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            vm.data?.subtitle?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.titleSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                            vm.data?.authors?.let {
                                Text(
                                    it.joinToString(", "),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall
                                        .copy(fontStyle = FontStyle.Italic)
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.horizontalScroll(genreScrollState)
                    ) {
                        vm.data?.genres?.forEach {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(it) },
                            )
                        }
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
                }
                Box(
                    Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopStart
                ) {
                    when (tabState) {
                        LocalTab.SUMMARY -> {
                            localFabState = DisplayedFab.EDIT_BOOK
                            if (vm.data?.description.isNullOrBlank()) {
                                Text(
                                    stringResource(R.string.no_description),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            } else {
                                Text(
                                    vm.data?.description ?: stringResource(R.string.no_description),
                                    modifier = Modifier
                                        .scrollable(
                                            summaryScrollState,
                                            orientation = Orientation.Vertical
                                        )
                                )
                            }
                        }
                        LocalTab.DETAILS -> {
                            localFabState = DisplayedFab.EDIT_BOOK
                            DetailsTab(
                                modifier = Modifier.verticalScroll(detailsScrollState),
                                data = vm.data
                            )
                        }
                        LocalTab.NOTE -> {
                            localFabState = if(hasNoteBeenModified)
                                DisplayedFab.SAVE_NOTE
                            else
                                DisplayedFab.NONE
                            OutlinedTextField(
                                value = vm.editedNote,
                                placeholder = { Text(stringResource(R.string.note_placeholder)) },

                                onValueChange = {
                                    vm.editedNote = it
                                    hasNoteBeenModified = true
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        val fabModifier = Modifier
            .align(Alignment.BottomEnd)
            .offset((-16).dp, (-16).dp)
        val iconModifier = Modifier
        LaunchedEffect(key1 = localFabState) {
            when (localFabState) {
                DisplayedFab.EDIT_BOOK -> {
                    vm.scaffoldState.fab = {
                        FAB(
                            onClick = {
                                vm.data?.bookId?.let {
                                    if (it > 0)
                                        navigator.navigate(EditBookPageDestination(it))
                                }
                            },
                            modifier = fabModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.edit_24px),
                                stringResource(R.string.edit_details),
                                modifier = iconModifier
                            )
                        }
                    }
                }
                DisplayedFab.SAVE_NOTE -> {
                    vm.scaffoldState.fab = {
                        FAB(
                            onClick = {
                                vm.data?.bookId?.let {
                                    vm.saveNote(it)
                                    hasNoteBeenModified = false
                                }
                            },
                            modifier = fabModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.save_24px),
                                stringResource(R.string.save),
                            )
                        }
                    }
                }
                DisplayedFab.NONE -> {
                    vm.scaffoldState.fab = {}
                }
            }
        }
    }
}

@Composable
@Preview(device = Devices.PIXEL_2, showSystemUi = true)
private fun ViewBookPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        ViewBookPage(
            3,
            object: IViewBookVM {
                override var editedNote = ""
                override val data: ViewBookImmutableData? =
                    ViewBookImmutableData(PreviewUtils.exampleBookBundle)
                override fun initFromLibraryBook(bookId: Long) {}
                override fun saveNote(bookId: Long) {}
                override val scaffoldState: ScaffoldState = ScaffoldState()
            },
            EmptyDestinationsNavigator
        )
    }
}