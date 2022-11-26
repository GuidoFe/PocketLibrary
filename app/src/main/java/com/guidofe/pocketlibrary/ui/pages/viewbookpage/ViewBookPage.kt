@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.guidofe.pocketlibrary.ui.pages.viewbookpage

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.EmptyBookCover
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.viewmodels.ViewBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import com.guidofe.pocketlibrary.viewmodels.previews.ViewBookVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

private enum class LocalTab { PROGRESS, SUMMARY, DETAILS, NOTE }

@Composable
private fun EditIcon(navigator: DestinationsNavigator, id: Long) {
    IconButton(
        onClick = {
            if (id > 0)
                navigator.navigate(EditBookPageDestination(id))
        },
    ) {
        Icon(
            painterResource(R.drawable.edit_24px),
            stringResource(R.string.edit_details),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ViewBookPage(
    bookId: Long? = null,
    vm: IViewBookVM = hiltViewModel<ViewBookVM>(),
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(context.getString(R.string.book_details))
        vm.scaffoldState.fab = {}
    }
    LaunchedEffect(key1 = true) {
        if (bookId != null) {
            vm.initFromLocalBook(bookId)
        }
    }

    var tabState by remember { mutableStateOf(LocalTab.PROGRESS) }
    val detailsScrollState = rememberScrollState()
    val summaryScrollState = rememberScrollState()
    val genreScrollState = rememberScrollState()
    var hasNoteBeenModified by remember { mutableStateOf(false) }
    var hasProgressBeenModified by remember { mutableStateOf(false) }

    LaunchedEffect(hasNoteBeenModified || hasProgressBeenModified) {
        if (hasNoteBeenModified || hasProgressBeenModified) {
            vm.scaffoldState.actions = {
                vm.bundle?.book?.bookId?.let {
                    EditIcon(navigator, it)
                }
                IconButton(
                    onClick = {
                        if (hasProgressBeenModified) {
                            vm.progTabState.isReadPagesError =
                                vm.progTabState.pagesReadString !=
                                vm.progTabState.pagesReadValue.toString()
                            vm.progTabState.isTotalPagesError =
                                vm.progTabState.totalPagesString !=
                                vm.progTabState.totalPagesValue.toString()
                            if (vm.progTabState.isReadPagesError ||
                                vm.progTabState.isTotalPagesError
                            ) {
                                return@IconButton
                            }
                            vm.saveProgress() {
                                hasProgressBeenModified = false
                            }
                        }
                        if (hasNoteBeenModified) {
                            vm.saveNote {
                                hasNoteBeenModified = false
                            }
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.save_24px),
                        stringResource(R.string.save),
                    )
                }
            }
        } else {
            vm.scaffoldState.actions = {
                vm.bundle?.book?.bookId?.let {
                    EditIcon(navigator, it)
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    Modifier
                        .widthIn(max = 80.dp)
                        .fillMaxHeight()
                ) {
                    var coverUri = vm.bundle?.book?.coverURI
                    if (coverUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(coverUri)
                                .build(),
                            contentDescription = stringResource(id = R.string.cover),
                            modifier = Modifier.fillMaxHeight()
                        )
                    } else {
                        EmptyBookCover(Modifier.fillMaxHeight())
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Column() {
                        Text(
                            vm.bundle?.book?.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        vm.bundle?.book?.subtitle?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        vm.bundle?.authors?.let { authorsList ->
                            val authorsString =
                                authorsList.joinToString(", ") { it.name }
                            Text(
                                authorsString,
                                style = MaterialTheme.typography.labelLarge
                                    .copy(fontStyle = FontStyle.Italic)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.horizontalScroll(genreScrollState)
                        ) {
                            vm.bundle?.genres?.forEach {
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            it.name,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
            ScrollableTabRow(
                selectedTabIndex = tabState.ordinal,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = tabState == LocalTab.PROGRESS,
                    onClick = { tabState = LocalTab.PROGRESS },
                    text = { Text(stringResource(R.string.progress_label)) }
                )
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
                    LocalTab.PROGRESS -> {
                        ProgressTab(
                            vm.progTabState
                        ) {
                            if (!hasProgressBeenModified)
                                hasProgressBeenModified = true
                        }
                    }
                    LocalTab.SUMMARY -> {
                        // TODO Fix unscrollable long summaries
                        if (vm.bundle?.book?.description.isNullOrBlank()) {
                            Text(
                                stringResource(R.string.no_description),
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        } else {
                            Text(
                                text = vm.bundle?.book?.description
                                    ?: stringResource(R.string.no_description),
                                modifier = Modifier.verticalScroll(summaryScrollState)
                            )
                        }
                    }
                    LocalTab.DETAILS -> {
                        DetailsTab(
                            modifier = Modifier.verticalScroll(detailsScrollState),
                            book = vm.bundle?.book
                        )
                    }
                    LocalTab.NOTE -> {
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
}

@Composable
@Preview(device = Devices.PIXEL_2, showSystemUi = true)
private fun ViewBookPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        ViewBookPage(
            3,
            ViewBookVMPreview(),
            EmptyDestinationsNavigator,
        )
    }
}