package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.guidofe.pocketlibrary.ui.modules.BookTile
import com.guidofe.pocketlibrary.ui.modules.ModalBottomSheet
import com.guidofe.pocketlibrary.ui.modules.PieChart
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.pages.destinations.BackupPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.CreditsPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.SettingsPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.theme.*
import com.guidofe.pocketlibrary.viewmodels.LandingPageVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILandingPageVM
import com.guidofe.pocketlibrary.viewmodels.previews.LandingPageVMPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Composable
private fun StatsTile(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        content = content
    )
}

@Composable
private fun LegendRow(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun NumberTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    StatsTile(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

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
            context.getString(R.string.home),
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
            verticalArrangement = Arrangement.spacedBy(padding)
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
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(padding)
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
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(padding),
                    modifier = Modifier.padding(padding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
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
                        modifier = Modifier.height(IntrinsicSize.Min)
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
                    BoxWithConstraints() {
                        StatsTile() {
                            Column {
                                Text(
                                    stringResource(R.string.owned_books),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                PieChart(
                                    circleThickness = 40.dp,
                                    innerCircleThickness = 40.dp,
                                    circleColor = MaterialTheme.colorScheme.surface,
                                    innerCircleColors = listOf(
                                        CustomGreen,
                                        CustomBlue,
                                        CustomYellow,
                                        CustomRed
                                    ),
                                    total = stats.libraryBooksCount.toFloat(),
                                    values = listOf(
                                        stats.libraryBooksRead.toFloat(),
                                        stats.libraryBooksCurrentlyReading.toFloat(),
                                        stats.libraryBooksSuspended.toFloat(),
                                        stats.libraryBooksDnf.toFloat()
                                    ),
                                    cap = StrokeCap.Butt,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        LegendRow(
                                            stringResource(R.string.read) +
                                                ": ${stats.libraryBooksRead}",
                                            color = CustomGreen
                                        )
                                        LegendRow(
                                            stringResource(R.string.currently_reading) +
                                                ": ${stats.libraryBooksCurrentlyReading}",
                                            color = CustomBlue
                                        )
                                        LegendRow(
                                            stringResource(R.string.suspended) +
                                                ": ${stats.libraryBooksSuspended}",
                                            color = CustomYellow
                                        )
                                        LegendRow(
                                            stringResource(R.string.did_not_finish) +
                                                ": ${stats.libraryBooksSuspended}",
                                            color = CustomRed
                                        )
                                        LegendRow(
                                            stringResource(R.string.not_read) +
                                                ": ${stats.libraryBooksNotRead}",
                                            color = MaterialTheme.colorScheme.surface
                                        )
                                    }
                                }
                            }
                        }
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
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = false) {
        LandingPage(
            EmptyDestinationsNavigator,
            LandingPageVMPreview(bookList = listOf())
        )
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreviewDark() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(
            EmptyDestinationsNavigator,
            LandingPageVMPreview()
        )
    }
}