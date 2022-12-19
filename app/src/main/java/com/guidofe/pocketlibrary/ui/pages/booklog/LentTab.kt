package com.guidofe.pocketlibrary.ui.pages.booklog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.LibraryBundle
import com.guidofe.pocketlibrary.data.local.library_db.entities.LentBook
import com.guidofe.pocketlibrary.ui.dialogs.CalendarDialog
import com.guidofe.pocketlibrary.ui.modules.RowWithIcon
import com.guidofe.pocketlibrary.ui.pages.destinations.EditBookPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.ViewBookPageDestination
import com.guidofe.pocketlibrary.ui.utils.Menu
import com.guidofe.pocketlibrary.ui.utils.MenuItem
import com.guidofe.pocketlibrary.ui.utils.SelectableListItem
import com.guidofe.pocketlibrary.ui.utils.rememberWindowInfo
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LentTab(
    lentItems: List<SelectableListItem<LibraryBundle>>,
    updateLent: (List<LentBook>) -> Unit,
    removeLentStatus: (books: List<LentBook>, callback: () -> Unit) -> Unit,
    state: LentTabState,
    navigator: DestinationsNavigator,
    setBottomSheetContent: (@Composable ColumnScope.() -> Unit) -> Unit,
    setBottomSheetVisibility: (Boolean, CoroutineScope) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val selectionManager = state.selectionManager
    val windowInfo = rememberWindowInfo()
    val contextMenu = remember {
        Menu<LibraryBundle>(
            menuItems = arrayOf(
                MenuItem(
                    labelId = { R.string.return_to_library },
                    iconId = { R.drawable.book_hand_left_24px },
                    onClick = { it.lent?.let { lent -> removeLentStatus(listOf(lent)) {} } }
                ),
                MenuItem(
                    labelId = { R.string.details },
                    iconId = { R.drawable.info_24px },
                    onClick = { navigator.navigate(ViewBookPageDestination(it.info.bookId)) }
                ),
                MenuItem(
                    labelId = { R.string.edit },
                    iconId = { R.drawable.edit_24px },
                    onClick = { navigator.navigate(EditBookPageDestination(it.info.bookId)) }
                )
            )
        )
    }
    LaunchedEffect(Unit) {
        if (!windowInfo.isBottomSheetLayout())
            return@LaunchedEffect
        setBottomSheetContent {
            val selectedItem = selectionManager.singleSelectedItem
            selectedItem?.let { item ->
                for (line in contextMenu.menuItems) {
                    RowWithIcon(
                        icon = {
                            Icon(painterResource(line.iconId(item)), null)
                        },
                        onClick = {
                            setBottomSheetVisibility(false, coroutineScope)
                            line.onClick(item)
                            state.selectionManager.singleSelectedItem = null
                        }
                    ) {
                        Text(
                            stringResource(line.labelId(item))
                        )
                    }
                }
            }
        }
    }
    Column(modifier = modifier) {
        if (lentItems.isEmpty())
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    stringResource(R.string.you_have_no_lent),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center)
                )
            }
        LazyColumn() {
            items(lentItems, key = { it.value.info.bookId }) { item ->
                var isDropdownMenuExpanded by remember { mutableStateOf(false) }
                var menuOffset by remember { mutableStateOf(DpOffset.Zero) }
                Box {
                    LentBookRow(
                        item,
                        onRowTap = {
                            if (selectionManager.isMultipleSelecting)
                                selectionManager.multipleSelectToggle(item.value)
                            else {
                                navigator.navigate(
                                    ViewBookPageDestination(item.value.info.bookId)
                                )
                            }
                        },
                        onCoverLongPress = {
                            if (!selectionManager.isMultipleSelecting)
                                selectionManager.startMultipleSelection(item.value)
                        },
                        onBorrowerTap = {
                            state.fieldToChange = LentField.BORROWER
                            selectionManager.singleSelectedItem = item.value
                            state.isBorrowerDialogVisible = true
                        },
                        onStartTap = {
                            state.fieldToChange = LentField.START
                            selectionManager.singleSelectedItem = item.value
                            state.isCalendarVisible = true
                        },
                        onRowLongPress = {
                            if (!selectionManager.isMultipleSelecting) {
                                if (windowInfo.isBottomSheetLayout()) {
                                    state.selectionManager.singleSelectedItem = item.value
                                    setBottomSheetVisibility(true, coroutineScope)
                                } else {
                                    menuOffset = it
                                    isDropdownMenuExpanded = true
                                }
                            }
                        },
                        areButtonsActive = !selectionManager.isMultipleSelecting,
                    ) {
                        if (!windowInfo.isBottomSheetLayout()) {
                            DropdownMenu(
                                expanded = isDropdownMenuExpanded,
                                onDismissRequest = { isDropdownMenuExpanded = false },
                                offset = menuOffset
                            ) {
                                for (line in contextMenu.menuItems) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(line.labelId(item.value))) },
                                        leadingIcon = {
                                            Icon(
                                                painterResource(line.iconId(item.value)),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            isDropdownMenuExpanded = false
                                            line.onClick(item.value)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Divider()
            }
        }
    }

    if (state.isBorrowerDialogVisible) {
        var textInput by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                state.fieldToChange = null
                selectionManager.clearSelection()
                state.isBorrowerDialogVisible = false
            },
            confirmButton = {
                Button(onClick = {
                    if (textInput.isBlank()) {
                        isError = true
                        return@Button
                    }
                    if (selectionManager.isMultipleSelecting) {
                        updateLent(
                            selectionManager.selectedItems.value.values.mapNotNull {
                                it.lent?.copy(who = textInput)
                            }
                        )
                    } else {
                        selectionManager.singleSelectedItem?.let { selected ->
                            state.isBorrowerDialogVisible = false
                            val newLent = selected.lent?.copy(
                                who = textInput
                            )
                            newLent?.let { updateLent(listOf(it)) }
                        }
                    }
                    selectionManager.clearSelection()
                    state.fieldToChange = null
                    state.isBorrowerDialogVisible = false
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    state.fieldToChange = null
                    selectionManager.clearSelection()
                    state.isBorrowerDialogVisible = false
                }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = {
                if (!state.selectionManager.isMultipleSelecting)
                    Text(selectionManager.singleSelectedItem?.bookBundle?.book?.title ?: "???")
            },
            text = {
                Column {
                    Text(stringResource(R.string.lent_to_colon))
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        isError = isError,
                        supportingText = {
                            if (isError) Text(stringResource(R.string.please_enter_value))
                        }
                    )
                }
            }
        )
    }
    if (state.isCalendarVisible) {
        CalendarDialog(
            onDismissed = {
                state.isCalendarVisible = false
                selectionManager.clearSelection()
                state.fieldToChange = null
            },
            startingDate = if (!state.isMultipleSelecting) {
                state.selectionManager.singleSelectedItem?.lent?.start?.let {
                    ZonedDateTime.ofInstant(it, ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()
            } else LocalDate.now()
        ) { newDate ->
            val convertedDate = newDate?.let {
                ZonedDateTime.of(
                    newDate, LocalTime.of(0, 0), ZoneId.systemDefault()
                ).toInstant()
            }
            if (convertedDate == null) {
                state.fieldToChange = null
                selectionManager.clearSelection()
                return@CalendarDialog
            }

            if (state.isMultipleSelecting) {
                val list = selectionManager.selectedItems.value.values.mapNotNull {
                    it.lent?.copy(start = convertedDate)
                }
                updateLent(list)
            } else {
                val info = selectionManager.singleSelectedItem?.lent?.copy(
                    start = convertedDate
                )
                info?.let { updateLent(listOf(it)) }
            }
            state.isCalendarVisible = false
            selectionManager.clearSelection()
            state.fieldToChange = null
        }
    }
}