package com.guidofe.pocketlibrary.ui.pages

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.PieChart
import com.guidofe.pocketlibrary.ui.pages.destinations.SettingsPageDestination
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
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
            .padding(padding)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(padding)
        ) {
            vm.stats?.let { stats ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    NumberTile(
                        label = stringResource(R.string.books_in_your_library),
                        value = stats.libraryBooksCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatsTile(
                        modifier = Modifier.weight(1f)
                    ) {
                        PieChart(
                            circleThickness = 20.dp,
                            innerCircleThickness = 20.dp,
                            circleColor = MaterialTheme.colorScheme.surface,
                            innerCircleColor = MaterialTheme.colorScheme.tertiary,
                            sweepAngle = if (stats.libraryBooksCount != 0)
                                stats.readBooksCount.toFloat() / stats.libraryBooksCount * 360f
                            else
                                0f,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text(
                                    stringResource(R.string.books_read),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    stats.readBooksCount.toString(),
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }
                    }
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