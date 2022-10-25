package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.SearchBookOnlinePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmallFabWithLabel(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(onClick = onClick) { Text(label, modifier = Modifier.padding(5.dp))}
        SmallFloatingActionButton(onClick = onClick) {
            Box(Modifier.size(24.dp)) {
                icon()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun enterTransition(duration: Int, delay: Int): EnterTransition {
    return scaleIn(
        tween(duration, delay),
        transformOrigin = TransformOrigin(1f, 1f)
    ) + fadeIn(tween(duration, delay))
}

@OptIn(ExperimentalAnimationApi::class)
fun exitTransition(duration: Int, delay: Int): ExitTransition {
    return scaleOut(
        tween(duration, delay),
        transformOrigin = TransformOrigin(1f, 1f)
    ) + fadeOut(tween(duration, delay))
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddBookFab(
    navigator: DestinationsNavigator,
    isExpanded: Boolean,
    onMainFabClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onSearchByIsbn: (isbn: String) -> Unit,
    modifier: Modifier = Modifier,
    stepDelay: Int = 100,
    stepDuration: Int = 100,
) {
    var showIsbnDialog: Boolean by remember {mutableStateOf(false)}
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.offset(x = (-7).dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = enterTransition(stepDuration * 4, stepDelay),
                exit = exitTransition(stepDuration * 1, stepDelay)
            ) {
                SmallFabWithLabel(
                    label = stringResource(R.string.insert_manually),
                    icon = {
                        Icon(
                            painterResource(R.drawable.edit_note_24px),
                            stringResource(R.string.insert_manually)
                        )
                    }
                ) {
                    onDismissRequest()
                    navigator.navigate(EditBookPageDestination())
                }
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = enterTransition(stepDuration * 3, stepDelay),
                exit = exitTransition(stepDuration * 2, stepDelay)
            ) {
                SmallFabWithLabel(
                    label = stringResource(R.string.type_the_isbn),
                    icon = {
                        Icon(
                            painterResource(R.drawable.type_code_24px),
                            stringResource(R.string.type_the_isbn)
                        )
                    }
                ) {
                    onDismissRequest()
                    showIsbnDialog = true
                }
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = enterTransition(stepDuration * 2, stepDelay),
                exit = exitTransition(stepDuration * 3, stepDelay)
            ) {
                SmallFabWithLabel(
                    label = stringResource(R.string.search_online),
                    icon = {
                        Icon(
                            painterResource(R.drawable.search_24px),
                            stringResource(R.string.search_online)
                        )
                    }
                ) {
                    onDismissRequest()
                    navigator.navigate(SearchBookOnlinePageDestination)
                }
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = enterTransition(stepDuration * 1, stepDelay),
                exit = exitTransition(stepDuration * 4, stepDelay)
            ) {
                SmallFabWithLabel(
                    label = stringResource(R.string.scan_isbn),
                    icon = {
                        Icon(
                            painterResource(R.drawable.barcode_scanner_24px),
                            stringResource(R.string.scan_isbn)
                        )
                    }
                ) {
                    onDismissRequest()
                    navigator.navigate(ScanIsbnPageDestination())
                }
            }
        }
        FAB(
            onClick = {onMainFabClick()}
        ) {
            Icon(
                painterResource(R.drawable.add_24px),
                stringResource(R.string.add_book)
            )
        }
    }
    if (showIsbnDialog) {
        InsertIsbnDialog(
            onConfirm = {
                showIsbnDialog = false
                onSearchByIsbn(it)
            },
            onDismiss = { showIsbnDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
private fun AddBookFabPreview() {
    MaterialTheme() {
        Scaffold(
            floatingActionButton = {
                AddBookFab(
                    navigator = EmptyDestinationsNavigator,
                    isExpanded = true,
                    onMainFabClick = {},
                    onDismissRequest = {},
                    onSearchByIsbn = {}
                )
            }
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}