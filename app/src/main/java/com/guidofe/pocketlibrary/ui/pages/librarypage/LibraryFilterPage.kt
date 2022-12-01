package com.guidofe.pocketlibrary.ui.pages.librarypage

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.repositories.LibraryQuery
import com.guidofe.pocketlibrary.ui.modules.DropdownBox
import com.guidofe.pocketlibrary.ui.modules.DropdownBoxWithTrailingButton
import com.guidofe.pocketlibrary.ui.modules.LanguageAutocomplete
import com.guidofe.pocketlibrary.ui.modules.OutlinedAutocomplete
import com.guidofe.pocketlibrary.viewmodels.LibraryFilterVM
import com.guidofe.pocketlibrary.viewmodels.interfaces.ILibraryFilterVM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Composable
private fun progressToString(progress: ProgressPhase?): String {
    return when (progress) {
        ProgressPhase.IN_PROGRESS -> stringResource(R.string.in_progress)
        ProgressPhase.SUSPENDED -> stringResource(R.string.suspended)
        ProgressPhase.READ -> stringResource(R.string.read)
        ProgressPhase.DNF -> stringResource(R.string.dnf)
        ProgressPhase.NOT_READ -> stringResource(R.string.not_read)
        null -> "-"
    }
}

@Composable
private fun mediaFilterToString(filter: LibraryQuery.MediaFilter): String {
    return when (filter) {
        LibraryQuery.MediaFilter.ANY -> stringResource(R.string.any)
        LibraryQuery.MediaFilter.ONLY_BOOKS -> stringResource(R.string.only_books)
        LibraryQuery.MediaFilter.ONLY_EBOOKS -> stringResource(R.string.only_ebooks)
    }
}

@Composable
private fun sortingFieldToString(field: LibraryQuery.LibrarySortField?): String {
    return when (field) {
        LibraryQuery.LibrarySortField.CREATION -> stringResource(R.string.creation_date)
        LibraryQuery.LibrarySortField.TITLE -> stringResource(R.string.title)
        null -> "-"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun LibraryFilterPage(
    startQuery: LibraryQuery?,
    vm: ILibraryFilterVM = hiltViewModel<LibraryFilterVM>(),
    navigator: ResultBackNavigator<LibraryQuery?>
) {
    val context = LocalContext.current
    LaunchedEffect(startQuery) {
        vm.initializeGenresList()
        vm.initializeState(startQuery)
    }
    LaunchedEffect(Unit) {
        vm.scaffoldState.refreshBar(
            title = context.getString(R.string.filter),
            navigationIcon = {
                IconButton(onClick = {
                    navigator.navigateBack()
                }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        stringResource(R.string.back)
                    )
                }
            }
        )
    }
    val scrollState = rememberScrollState()
    val innerPadding = 10.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(innerPadding),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            OutlinedTextField(
                value = vm.title,
                onValueChange = { vm.title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = vm.author,
                onValueChange = { vm.author = it },
                label = { Text(stringResource(R.string.author)) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            OutlinedAutocomplete(
                text = vm.genre,
                onTextChange = { vm.genre = it },
                options = vm.genres,
                label = { Text(stringResource(R.string.genre), maxLines = 1) },
                onOptionSelected = { vm.genre = it },
                threshold = 1,
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Min)
            )
            LanguageAutocomplete(
                text = vm.language,
                onTextChange = { vm.language = it },
                label = { Text(stringResource(R.string.language), maxLines = 1) },
                onOptionSelected = { vm.language = it },
                showTrailingIcon = false,
                modifier = Modifier
                    .weight(1f)
                    .height(IntrinsicSize.Min)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            Text(
                stringResource(R.string.show_only_favorites),
                modifier = Modifier.weight(1f)
            )
            Switch(checked = vm.onlyFavorite, onCheckedChange = {
                vm.onlyFavorite = !vm.onlyFavorite
            })
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            Text(
                stringResource(R.string.media_type),
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = vm.isMediaDropdownExpanded,
                onExpandedChange = {
                    vm.isMediaDropdownExpanded = !vm.isMediaDropdownExpanded
                },
                modifier = Modifier.wrapContentWidth() // .weight(1f)
            ) {
                DropdownBox(
                    text = { Text(mediaFilterToString(vm.mediaFilter)) },
                    isExpanded = vm.isMediaDropdownExpanded,
                    modifier = Modifier.menuAnchor()
                )
                DropdownMenu(
                    expanded = vm.isMediaDropdownExpanded,
                    onDismissRequest = { vm.isMediaDropdownExpanded = false }
                ) {
                    for (value in LibraryQuery.MediaFilter.values()) {
                        DropdownMenuItem(
                            text = { Text(mediaFilterToString(value)) },
                            onClick = {
                                vm.mediaFilter = value
                                vm.isMediaDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            Text(
                stringResource(R.string.progress_label),
                modifier = Modifier.weight(1f)
            )
            ExposedDropdownMenuBox(
                expanded = vm.isProgressDropdownExpanded,
                onExpandedChange = {
                    vm.isProgressDropdownExpanded = !vm.isProgressDropdownExpanded
                },
                modifier = Modifier // .weight(1f)
            ) {
                DropdownBox(
                    text = { Text(progressToString(vm.progress)) },
                    isExpanded = vm.isProgressDropdownExpanded,
                    modifier = Modifier.menuAnchor()
                )
                DropdownMenu(
                    expanded = vm.isProgressDropdownExpanded,
                    onDismissRequest = { vm.isProgressDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("-") },
                        onClick = {
                            vm.progress = null
                            vm.isProgressDropdownExpanded = false
                        }
                    )
                    for (value in ProgressPhase.values()) {
                        DropdownMenuItem(
                            text = { Text(progressToString(value)) },
                            onClick = {
                                vm.progress = value
                                vm.isProgressDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(innerPadding)
        ) {
            Text(stringResource(R.string.sort_by))
            ExposedDropdownMenuBox(
                expanded = vm.isSortDropdownExpanded,
                onExpandedChange = { vm.isSortDropdownExpanded = !vm.isSortDropdownExpanded },
            ) {
                DropdownBoxWithTrailingButton(
                    text = { Text(sortingFieldToString(vm.sortingField)) },
                    enabled = vm.sortingField != null,
                    icon = {
                        if (vm.isOrderReversed)
                            Icon(
                                painterResource(R.drawable.arrow_upward_24px),
                                stringResource(R.string.descending)
                            )
                        else
                            Icon(
                                painterResource(R.drawable.arrow_downward_24px),
                                stringResource(R.string.ascending)
                            )
                    },
                    buttonColor = MaterialTheme.colorScheme.tertiary,
                    onIconClick = { vm.isOrderReversed = !vm.isOrderReversed },
                    modifier = Modifier.menuAnchor()
                )
                DropdownMenu(
                    expanded = vm.isSortDropdownExpanded,
                    onDismissRequest = { vm.isSortDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("-") },
                        onClick = {
                            vm.sortingField = null
                            vm.isSortDropdownExpanded = false
                        }
                    )
                    for (value in LibraryQuery.LibrarySortField.values()) {
                        DropdownMenuItem(
                            text = { Text(sortingFieldToString(value)) },
                            onClick = {
                                vm.sortingField = value
                                vm.isSortDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(innerPadding),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            OutlinedButton(
                onClick = { vm.initializeState(null) }
            ) {
                Text(stringResource(R.string.clear))
            }
            Button(
                onClick = { navigator.navigateBack(vm.createQuery()) }
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    }
}