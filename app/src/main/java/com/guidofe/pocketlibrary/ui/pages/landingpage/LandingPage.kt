package com.guidofe.pocketlibrary.ui.pages.landingpage

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.modules.BookTile
import com.guidofe.pocketlibrary.ui.modules.ModalBottomSheet
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.pages.destinations.BackupPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.CreditsPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.SettingsPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.theme.*
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils
import com.guidofe.pocketlibrary.ui.utils.WindowType
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.guidofe.pocketlibrary.viewmodels.LandingPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM
import com.guidofe.pocketlibrary.viewmodels.previews.LandingPageVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    vm: ILandingPageVM = hiltViewModel<LandingPageVM>(),
) {
    val padding = 8.dp
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val windowInfo = rememberWindowInfo()
    vm.scaffoldState.scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showMoreMenu by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                vm.initStats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            title = { Text(stringResource(R.string.home)) },
            actions = {
                IconButton(
                    onClick = {
                        showMoreMenu = true
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.more_vert_24px),
                        stringResource(R.string.more)
                    )
                }
            }
        )
    }
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .nestedScroll(vm.scaffoldState.scrollBehavior!!.nestedScrollConnection)
            .verticalScroll(scroll)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            vm.stats?.let { stats ->
                if (stats.booksCurrentlyReading.isEmpty()) {
                    Text(
                        stringResource(R.string.currently_not_reading_books),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    )
                } else {
                    Text(
                        stringResource(
                            R.string.books_currently_reading
                        ),
                        modifier = Modifier.padding(padding)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.Center,
                        contentPadding = PaddingValues(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(stats.booksCurrentlyReading) { item ->
                            BookTile(
                                item,
                                modifier = Modifier.shadow(
                                    elevation = 6.dp,
                                    shape = MaterialTheme.shapes.small
                                )
                            ) {
                                navigator.navigate(ViewBookPageDestination(item.book.bookId))
                            }
                            Spacer(Modifier.width(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (windowInfo.screenWidthInfo == WindowType.COMPAT) {
                    StatsQuarters(stats, modifier = Modifier.heightIn(max = 300.dp))
                    PieStatsTile(stats)
                } else {
                    Row(modifier = Modifier.size(600.dp, 296.dp)) {
                        StatsQuarters(
                            stats,
                            modifier = Modifier
                                .weight(1f).fillMaxHeight()
                        )
                        Spacer(Modifier.width(8.dp))
                        PieStatsTile(stats, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
    ModalBottomSheet(
        showMoreMenu,
        onDismiss = { showMoreMenu = false }
    ) {
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.settings_24px),
                    stringResource(R.string.settings)
                )
            },
            onClick = {
                navigator.navigate(SettingsPageDestination)
            }
        ) {
            Text(stringResource(R.string.settings))
        }
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.info_24px),
                    stringResource(R.string.about)
                )
            },
            onClick = {
                navigator.navigate(CreditsPageDestination)
            }
        ) {
            Text(stringResource(R.string.about))
        }
        RowWithIcon(
            icon = {
                Icon(
                    painterResource(R.drawable.settings_backup_restore_24px),
                    stringResource(R.string.backup_restore)
                )
            },
            onClick = {
                navigator.navigate(BackupPageDestination)
            }
        ) {
            Text(stringResource(R.string.backup_restore))
        }
    }
}

@Composable
fun StatsQuarters(stats: AppStats, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            NumberTile(
                label = stringResource(R.string.books_in_your_library),
                value = stats.libraryBooksCount.toString(),
                modifier = Modifier.weight(1f)
            )
            NumberTile(
                label = stringResource(R.string.books_read),
                value = stats.totalReadBooksCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            NumberTile(
                label = stringResource(R.string.borrowed_books),
                value = stats.currentlyBorrowedBooksCount.toString(),
                modifier = Modifier.weight(1f)
            )
            NumberTile(
                label = stringResource(R.string.lent_books),
                value = stats.lentBooksCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
@Preview(device = Devices.PHONE, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        LandingPage(
            EmptyDestinationsNavigator,
            LandingPageVMPreview(
                bookList = listOf(
                    PreviewUtils.exampleBookBundle,
                    PreviewUtils.exampleBookBundle,
                    PreviewUtils.exampleBookBundle,
                )
            )
        )
    }
}