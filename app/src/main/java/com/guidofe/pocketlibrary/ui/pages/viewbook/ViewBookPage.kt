@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.guidofe.pocketlibrary.ui.pages.viewbook

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BookBundle
import com.guidofe.pocketlibrary.ui.dialogs.ConfirmExitDialog
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.LibraryPageDestination
import com.guidofe.pocketlibrary.ui.pages.library.LibraryPageNavArgs
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import com.guidofe.pocketlibrary.ui.utils.WindowType
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.guidofe.pocketlibrary.viewmodels.ViewBookVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.IViewBookVM
import com.guidofe.pocketlibrary.viewmodels.previews.ViewBookVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch

internal enum class LocalTab { PROGRESS, SUMMARY, DETAILS, NOTE }

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
    var tabState by remember { mutableStateOf(LocalTab.PROGRESS) }
    var hasNoteBeenModified by remember { mutableStateOf(false) }
    var hasProgressBeenModified by remember { mutableStateOf(false) }
    var showTitlePopup by remember { mutableStateOf(false) }
    var showConfirmExitDialog by remember { mutableStateOf(false) }
    val windowInfo = rememberWindowInfo()

    LaunchedEffect(key1 = true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.book_details)) },
            navigationIcon = {
                IconButton(onClick = {
                    if (hasProgressBeenModified || hasNoteBeenModified)
                        showConfirmExitDialog = true
                    else
                        navigator.navigateUp()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
        vm.scaffoldState.fab = {}
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                coroutineScope.launch {
                    if (bookId != null) vm.initFromLocalBook(bookId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                                Log.e("debug", "ViewBook: Read pages error")
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
        when {
            windowInfo.screenHeightInfo == WindowType.COMPAT &&
                windowInfo.screenWidthInfo == WindowType.COMPAT -> {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                ) {
                    vm.bundle?.let { bundle ->
                        HorizontalCompactBookHeading(
                            bundle = bundle,
                            onHeadingClick = { showTitlePopup = true },
                            onGenreClick = {
                                navigator.navigate(
                                    LibraryPageDestination(
                                        LibraryPageNavArgs(genre = it)
                                    )
                                )
                            }
                        )
                    }
                    ViewBookTabRow(tabState) { tabState = it }
                    TabHost(
                        bundle = vm.bundle,
                        tabState = tabState,
                        progTabState = vm.progTabState,
                        editedNote = vm.editedNote,
                        onEditedNoteChange = {
                            vm.editedNote = it
                            hasNoteBeenModified = true
                        },
                        onProgressChanged = { hasProgressBeenModified = true },
                        areTabsScrollable = false
                    )
                }
            }
            windowInfo.screenHeightInfo == WindowType.COMPAT -> {
                Row {
                    vm.bundle?.let { bundle ->
                        VerticalCompactBookHeading(
                            bundle = bundle,
                            onHeadingClick = { showTitlePopup = true },
                            onGenreClick = {
                                navigator.navigate(
                                    LibraryPageDestination(
                                        LibraryPageNavArgs(genre = it)
                                    )
                                )
                            },
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                        Column(
                            // modifier = Modifier.weight(1f)
                        ) {
                            ViewBookTabRow(tabState) { tabState = it }
                            TabHost(
                                bundle = vm.bundle,
                                tabState = tabState,
                                progTabState = vm.progTabState,
                                editedNote = vm.editedNote,
                                onEditedNoteChange = {
                                    vm.editedNote = it
                                    hasNoteBeenModified = true
                                },
                                onProgressChanged = { hasProgressBeenModified = true },
                                areTabsScrollable = true
                            )
                        }
                    }
                }
            }
            windowInfo.screenWidthInfo == WindowType.COMPAT -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    vm.bundle?.let { bundle ->
                        HorizontalCompactBookHeading(
                            bundle = bundle,
                            onHeadingClick = { showTitlePopup = true },
                            onGenreClick = {
                                navigator.navigate(
                                    LibraryPageDestination(
                                        LibraryPageNavArgs(genre = it)
                                    )
                                )
                            }
                        )
                    }
                    ViewBookTabRow(tabState) { tabState = it }
                    Box(
                        Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopStart
                    ) {
                        TabHost(
                            bundle = vm.bundle,
                            tabState = tabState,
                            progTabState = vm.progTabState,
                            editedNote = vm.editedNote,
                            onEditedNoteChange = {
                                vm.editedNote = it
                                hasNoteBeenModified = true
                            },
                            onProgressChanged = { hasProgressBeenModified = true },
                            areTabsScrollable = true
                        )
                    }
                }
            } else -> {
            }
        }
    }
    if (showTitlePopup) {
        Dialog(
            onDismissRequest = { showTitlePopup = false },
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)

                ) {
                    SelectionContainer() {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            vm.bundle?.book?.title?.let {
                                Text(
                                    it,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            vm.bundle?.book?.subtitle?.let {
                                Text(
                                    it,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                            vm.bundle?.authors?.joinToString(", ") { it.name }?.let {
                                Text(
                                    it,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Thin
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { showTitlePopup = false },
                        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            }
        }
    }
    if (showConfirmExitDialog) {
        ConfirmExitDialog(
            onCancel = { showConfirmExitDialog = false },
            onConfirm = { navigator.navigateUp() }
        )
    }
}

@Composable
internal fun TabHost(
    bundle: BookBundle?,
    tabState: LocalTab,
    progTabState: ProgressTabState,
    editedNote: String,
    areTabsScrollable: Boolean,
    onEditedNoteChange: (String) -> Unit,
    onProgressChanged: () -> Unit
) {
    when (tabState) {
        LocalTab.PROGRESS -> {
            ProgressTab(
                progTabState,
                isScrollable = areTabsScrollable
            ) {
                onProgressChanged()
            }
        }
        LocalTab.SUMMARY -> {
            SummaryTab(
                bundle?.book?.description,
                modifier = Modifier.fillMaxSize(),
                isScrollable = areTabsScrollable
            )
        }
        LocalTab.DETAILS -> {
            DetailsTab(
                book = bundle?.book,
                isScrollable = areTabsScrollable
            )
        }
        LocalTab.NOTE -> {
            NoteTab(
                value = editedNote,
                onValueChange = {
                    onEditedNoteChange(it)
                }
            )
        }
    }
}

@Composable
internal fun ViewBookTabRow(tabState: LocalTab, setTabState: (LocalTab) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = tabState.ordinal,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Tab(
            selected = tabState == LocalTab.PROGRESS,
            onClick = { setTabState(LocalTab.PROGRESS) },
            text = { Text(stringResource(R.string.progress_label)) }
        )
        Tab(
            selected = tabState == LocalTab.SUMMARY,
            onClick = { setTabState(LocalTab.SUMMARY) },
            text = { Text(stringResource(R.string.summary)) }
        )
        Tab(
            selected = tabState == LocalTab.DETAILS,
            onClick = { setTabState(LocalTab.DETAILS) },
            text = { Text(stringResource(R.string.details)) }
        )
        Tab(
            selected = tabState == LocalTab.NOTE,
            onClick = { setTabState(LocalTab.NOTE) },
            text = { Text(stringResource(R.string.note)) }
        )
    }
}

@Composable
@Preview(device = Devices.PHONE, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
private fun ViewBookPagePreviewDark() {
    PocketLibraryTheme(darkTheme = true) {
        ViewBookPage(
            3,
            ViewBookVMPreview(),
            EmptyDestinationsNavigator,
        )
    }
}