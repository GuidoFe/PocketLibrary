package com.guidofe.pocketlibrary.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.modules.TimePickerDialog
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NotificationDialogFlow(
    onDismissRequest: () -> Unit,
    onConfirm: (Instant?) -> Unit,
    startingInstant: Instant,
    startingEnabled: Boolean
) {
    var isMainDialogOpen by remember { mutableStateOf(true) }
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var isTimePickerOpen by remember { mutableStateOf(false) }
    var isNotificationEnabled by remember { mutableStateOf(startingEnabled) }
    var date by remember {
        mutableStateOf(
            ZonedDateTime.ofInstant(startingInstant, ZoneId.systemDefault())
                .toLocalDate()
        )
    }
    var time by remember {
        mutableStateOf(
            ZonedDateTime.ofInstant(startingInstant, ZoneId.systemDefault())
                .toLocalTime()
        )
    }

    if (isMainDialogOpen) {
        NotificationDialog(
            onDismissRequest = onDismissRequest,
            onConfirm = {
                if (isNotificationEnabled) {
                    val instant = ZonedDateTime.of(date, time, ZoneId.systemDefault()).toInstant()
                    onConfirm(instant)
                } else
                    onConfirm(null)
            },
            onDateClick = {
                isMainDialogOpen = false
                isDatePickerOpen = true
            },
            onTimeClick = {
                isMainDialogOpen = false
                isTimePickerOpen = true
            },
            date = date,
            time = time,
            isNotificationEnabled = isNotificationEnabled,
            onNotificationToggle = { isNotificationEnabled = !isNotificationEnabled }
        )
    }

    if (isDatePickerOpen) {
        CalendarDialog(
            onDismissed = {
                isDatePickerOpen = false
                isMainDialogOpen = true
            },
            onDaySelected = {
                it?.let { selectedDate ->
                    date = selectedDate
                    isDatePickerOpen = false
                    isMainDialogOpen = true
                }
            }
        )
    }

    if (isTimePickerOpen) {
        TimePickerDialog(
            startingHour = time.hour,
            startingMinutes = time.minute,
            onCancel = {
                isTimePickerOpen = false
                isMainDialogOpen = true
            },
            onTimePicked = { h, m ->
                time = LocalTime.of(h, m)
                isTimePickerOpen = false
                isMainDialogOpen = true
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NotificationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onNotificationToggle: () -> Unit,
    isNotificationEnabled: Boolean,
    date: LocalDate,
    time: LocalTime
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.enable_notification),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = {
                            onNotificationToggle()
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.date_colon),
                        color = textColor(isNotificationEnabled),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        enabled = isNotificationEnabled,
                        onClick = onDateClick
                    ) {
                        Text(
                            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.time_colon),
                        color = textColor(isNotificationEnabled),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        enabled = isNotificationEnabled,
                        onClick = onTimeClick
                    ) {
                        Text(
                            time.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                }
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
private fun textColor(isNotificationEnabled: Boolean): Color {
    return if (isNotificationEnabled)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
}

@Composable
@Preview(device = Devices.PHONE, showSystemUi = true)
@Preview(device = Devices.TABLET, showSystemUi = true)
private fun NotificationDialogPreview() {
    PocketLibraryTheme {
        NotificationDialog(
            onDismissRequest = {},
            onConfirm = { /*TODO*/ },
            onDateClick = {},
            onTimeClick = {},
            onNotificationToggle = {},
            isNotificationEnabled = true,
            date = LocalDate.now(),
            time = LocalTime.now()
        )
    }
}
