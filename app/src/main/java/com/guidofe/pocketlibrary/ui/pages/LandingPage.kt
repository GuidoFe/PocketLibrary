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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.BookTile
import com.guidofe.pocketlibrary.ui.modules.PieChart
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
            .padding(10.dp),
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
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
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

@RootNavGraph(start = true)
@Destination
@Composable
@ExperimentalGetImage
fun LandingPage(
    navigator: DestinationsNavigator,
    vm: ILandingPageVM = hiltViewModel<LandingPageVM>(),
) {
    val padding = 10.dp
    val context = LocalContext.current
    val scroll = rememberScrollState()
    LaunchedEffect(true) {
        vm.scaffoldState.refreshBar(
            context.getString(R.string.home),
            actions = {
                IconButton(
                    onClick = {
                        navigator.navigate(SettingsPageDestination)
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.settings_24px),
                        stringResource(R.string.settings)
                    )
                }
            }
        )
        vm.initStats()
    }
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scroll)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(padding)
        ) {
            vm.stats?.let { stats ->
                Text(
                    stringResource(
                        R.string.books_currently_reading
                    ),
                    modifier = Modifier.padding(padding)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
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
                Spacer(Modifier.height(10.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(padding),
                    modifier = Modifier.padding(padding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
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
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun LandingPagePreview() {
    PocketLibraryTheme(darkTheme = true) {
        LandingPage(
            EmptyDestinationsNavigator,
            LandingPageVMPreview()
        )
    }
}