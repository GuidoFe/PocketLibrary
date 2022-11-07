package com.guidofe.pocketlibrary.ui.dialogs

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.guidofe.pocketlibrary.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

private enum class DayState{NORMAL, TODAY, SELECTED}

@Composable
private fun DayCell(text: String, cellStatus: DayState = DayState.NORMAL, date: LocalDate? = null,
                    onClick: (LocalDate?) -> Unit = {}) {
    var m = Modifier.size(40.dp)
    when (cellStatus) {
        DayState.TODAY -> m = m.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        DayState.SELECTED -> m = m
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
        else -> {}
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = m.clickable{onClick(date)}
    ) {
        Text(
            text,
            color = if (cellStatus == DayState.SELECTED)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun TextCell(text: String = "") {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarPicker(
    locale: Locale,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    setSelectedDate: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    var year by remember{mutableStateOf(selectedDate.year)}
    var month by remember{mutableStateOf(selectedDate.month)}
    var isYearSelectionExpanded by remember{mutableStateOf(false)}
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${month.getDisplayName(TextStyle.FULL, locale)} $year"
            )
            IconButton(onClick = { isYearSelectionExpanded = !isYearSelectionExpanded }) {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isYearSelectionExpanded)
            }
            Spacer(Modifier.weight(1f))
            if (!isYearSelectionExpanded) {
                Row() {
                    IconButton(onClick = {
                        month = month.minus(1)
                        if (month == Month.DECEMBER)
                            year--
                    }) {
                        Icon(
                            painterResource(R.drawable.chevron_left_24px),
                            stringResource(R.string.previous_month)
                        )
                    }
                    IconButton(onClick = {
                        month = month.plus(1)
                        if (month == Month.JANUARY)
                            year++
                    }) {
                        Icon(
                            painterResource(R.drawable.chevron_right_24px),
                            stringResource(R.string.next_month)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isYearSelectionExpanded) {
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                items(100){ index ->
                    val y = index + today.year - 4
                    Box(
                        modifier = Modifier
                            .size(72.dp, 36.dp)
                            .clip(RoundedCornerShape(100))
                            .background(
                                if (y == year)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent
                            )
                            .clickable {
                                year = y
                                isYearSelectionExpanded = false
                            }
                    ) {
                        Text(
                            y.toString(),
                            color = if (y == year)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        } else {
            Row() {
                for(i in 1..7) {
                    TextCell(DayOfWeek.of(i).getDisplayName(TextStyle.SHORT, locale))
                }
            }
            var firstMonthDay = LocalDate.of(year, month, 1)
            var i = LocalDate.of(year, month, 1)
                .minusDays((firstMonthDay.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
            while(i.month == month || i < firstMonthDay) {
                Row() {
                    for (j in 1..7) {
                        val dayState = when (i) {
                            selectedDate -> DayState.SELECTED
                            today -> DayState.TODAY
                            else -> DayState.NORMAL
                        }
                        if (i.month == month)
                            DayCell(i.dayOfMonth.toString(), dayState, i) {
                                it?.let { date ->
                                    setSelectedDate(date)
                                }
                            }
                        else
                            TextCell()
                        i = i.plusDays(1)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CalendarDialog(
    onDismissed: () -> Unit,
    hasClearOption: Boolean = false,
    startingDate: LocalDate = LocalDate.now(),
    onDaySelected: (LocalDate?) -> Unit,
) {
    var selectedDay: LocalDate by remember{mutableStateOf(startingDate)}
    var isPickerOpen by remember{mutableStateOf(true)}
    var dateInput by remember{mutableStateOf("")}
    var isDateInputError by remember{mutableStateOf(false)}
    val context = LocalContext.current
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
    Dialog(
        onDismissRequest = {onDismissed()},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.width(304.dp)
        ) {
            Column() {
                Column(
                    modifier = Modifier.padding(24.dp, 16.dp)

                ) {
                    Text(
                        stringResource(R.string.select_date),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    Row() {
                        Text(
                            if (isPickerOpen)
                                selectedDay.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                            else
                                stringResource(R.string.enter_date),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            isPickerOpen = !isPickerOpen
                        }) {
                            Icon(
                                painterResource(
                                    if(isPickerOpen)
                                        R.drawable.edit_24px
                                    else
                                        R.drawable.calendar_today_24px
                                ),
                                stringResource(R.string.edit)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(if (isPickerOpen) 12.dp else 4.dp))
                if (isPickerOpen)
                    CalendarPicker(
                        locale,
                        selectedDay,
                        setSelectedDate = {selectedDay = it},
                        modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 12.dp) )
                else {
                    OutlinedTextField(
                        value = dateInput,
                        isError = isDateInputError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { dateInput = it },
                        label = { Text(stringResource(R.string.date)) },
                        placeholder = { Text(stringResource(R.string.yyyymmdd)) },
                        supportingText = {
                            if(isDateInputError)
                                Text(stringResource(R.string.error_date_not_valid))
                        },
                        modifier = Modifier.padding(24.dp, 6.dp, 24.dp, 16.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.align(Alignment.End).padding(12.dp, 0.dp, 12.dp, 12.dp)
                ){
                    TextButton(
                        onClick = onDismissed
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(Modifier.width(16.dp))
                    if (hasClearOption) {
                        TextButton(onClick = {onDaySelected(null)}) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                    TextButton(
                        onClick = {
                            if(isPickerOpen) {
                                onDaySelected(selectedDay)
                            }
                            else {
                                try {
                                    val date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_DATE)
                                    onDaySelected(date)
                                    isDateInputError = false
                                } catch(e: DateTimeParseException) {
                                    dateInput = ""
                                    isDateInputError = true
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}