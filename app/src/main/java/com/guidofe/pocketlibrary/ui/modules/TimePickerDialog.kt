package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimePickerDialog(
    startingHour: Int,
    startingMinutes: Int,
    onCancel: () -> Unit,
    onTimePicked: (hours: Int, minutes: Int) -> Unit
) {
    Dialog(
        onDismissRequest = { onCancel() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var hourError by remember { mutableStateOf(false) }
        var minuteError by remember { mutableStateOf(false) }
        var hourField by remember {
            mutableStateOf(startingHour.toString().padStart(2, '0'))
        }
        var minuteField by remember {
            mutableStateOf(startingMinutes.toString().padStart(2, '0'))
        }
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 3.dp,
            shadowElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    stringResource(R.string.enter_time),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        TimeField(
                            value = hourField,
                            onValueChange = { if (it.length <= 2) hourField = it },
                            modifier = Modifier.size(128.dp, 72.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            isError = hourError
                        )
                        Text(
                            stringResource(R.string.hour),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column {
                        Text(
                            ":",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Column {
                        TimeField(
                            value = minuteField,
                            onValueChange = { if (it.length <= 2) minuteField = it },
                            modifier = Modifier.size(128.dp, 72.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            isError = minuteError
                        )
                        Text(
                            stringResource(R.string.minute),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(R.drawable.clock_24px),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        TextButton(
                            onClick = { onCancel() }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                val hourInt = hourField.toIntOrNull()
                                val minuteInt = minuteField.toIntOrNull()
                                if (isHourValid(hourInt) && isMinuteValid(minuteInt)) {
                                    onTimePicked(hourInt!!, minuteInt!!)
                                } else {
                                    if (!isHourValid(hourInt))
                                        hourError = true
                                    if (!isMinuteValid(minuteInt))
                                        minuteError = true
                                }
                            }
                        ) {
                            Text(stringResource(R.string.ok_label))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged {
                isFocused = it.hasFocus
            },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        textStyle = MaterialTheme.typography.displayMedium.copy(
            textAlign = TextAlign.Center,
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else if (isFocused)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { text ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isError)
                            MaterialTheme.colorScheme.errorContainer
                        else if (isFocused)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.small
                    )
                    .then(
                        if (isFocused)
                            Modifier.border(
                                1.dp,
                                if (isError)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary,
                                MaterialTheme.shapes.small
                            ) else
                            Modifier
                    )

            ) {
                text()
            }
        }

    )
}

private fun isHourValid(hour: Int?): Boolean {
    return hour != null && hour in 0..23
}

private fun isMinuteValid(minute: Int?): Boolean {
    return minute != null && minute in 0..59
}

@Preview
@Composable
private fun TimePickerDialogPreview() {
    PocketLibraryTheme(darkTheme = false) {
        TimePickerDialog(
            startingHour = 8,
            startingMinutes = 0,
            onCancel = {},
            onTimePicked = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun TimePickerDialogPreviewDarkTheme() {
    PocketLibraryTheme(darkTheme = true) {
        TimePickerDialog(
            startingHour = 8,
            startingMinutes = 0,
            onCancel = {},
            onTimePicked = { _, _ -> }
        )
    }
}