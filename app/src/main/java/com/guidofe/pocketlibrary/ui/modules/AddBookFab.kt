package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ScanIsbnPageDestination
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


@Composable
fun AddBookFab(
    navigator: DestinationsNavigator,
    isExpanded: Boolean,
    onMainFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.offset(x = (-7).dp)
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
                    navigator.navigate(EditBookPageDestination())
                }
                SmallFabWithLabel(
                    label = stringResource(R.string.type_the_isbn),
                    icon = {
                        Icon(
                            painterResource(R.drawable.type_code_24px),
                            stringResource(R.string.type_the_isbn)
                        )
                    }
                ) {
                    //TODO
                }
                SmallFabWithLabel(
                    label = stringResource(R.string.search_title_online),
                    icon = {
                        Icon(
                            painterResource(R.drawable.search_24px),
                            stringResource(R.string.search_title_online)
                        )
                    }
                ) {
                    //TODO
                }
                SmallFabWithLabel(
                    label = stringResource(R.string.scan_isbn),
                    icon = {
                        Icon(
                            painterResource(R.drawable.barcode_scanner_24px),
                            stringResource(R.string.scan_isbn)
                        )
                    }
                ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun AddBookFabPreview() {
    MaterialTheme() {
        Scaffold(
            floatingActionButton = {
                AddBookFab(navigator = EmptyDestinationsNavigator, isExpanded = true, onMainFabClick = {})
            }
        ) {
            Box(modifier = Modifier.padding(it))
        }
    }
}