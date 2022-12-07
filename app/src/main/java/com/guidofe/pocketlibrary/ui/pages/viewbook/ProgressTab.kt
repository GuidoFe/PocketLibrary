package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import java.lang.Integer.max

@Composable
private fun progressToString(progress: ProgressPhase?): String {
    return when (progress) {
        ProgressPhase.IN_PROGRESS -> stringResource(R.string.in_progress)
        ProgressPhase.SUSPENDED -> stringResource(R.string.suspended)
        ProgressPhase.READ -> stringResource(R.string.read)
        ProgressPhase.DNF -> stringResource(R.string.dnf)
        else -> stringResource(R.string.not_read)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTab(
    state: ProgressTabState,
    modifier: Modifier = Modifier,
    onValuesChanged: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = state.isDropdownExpanded,
            onExpandedChange = { state.isDropdownExpanded = !state.isDropdownExpanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = progressToString(state.selectedPhase),
                readOnly = true,
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(state.isDropdownExpanded)
                },
            )
            ExposedDropdownMenu(
                expanded = state.isDropdownExpanded,
                onDismissRequest = { state.isDropdownExpanded = false }
            ) {
                ProgressPhase.values().forEach {
                    DropdownMenuItem(
                        text = { Text(progressToString(it)) },
                        onClick = {
                            if (state.selectedPhase == ProgressPhase.NOT_READ)
                                state.selectedPhase = null
                            else
                                state.selectedPhase = it
                            state.isDropdownExpanded = false
                            onValuesChanged()
                        }
                    )
                }
            }
        }
        if (state.selectedPhase != null && state.selectedPhase != ProgressPhase.READ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.track_pages), modifier = Modifier.weight(1f))
                Switch(state.trackPages, {
                    state.trackPages = it
                    onValuesChanged()
                })
            }
            if (state.trackPages) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    OutlinedTextField(
                        value = state.pagesReadString,
                        label = { Text(stringResource(R.string.pages_read)) },
                        onValueChange = { s ->
                            state.changePagesReadString(s)
                            onValuesChanged()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = state.isReadPagesError,
                        supportingText = {
                            if (state.isReadPagesError)
                                Text(stringResource(R.string.number_not_valid))
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.totalPagesString,
                        label = { Text(stringResource(R.string.total_pages)) },
                        onValueChange = { s ->
                            state.changeTotalPagesString(s)
                            onValuesChanged()
                        },
                        isError = state.isTotalPagesError,
                        supportingText = {
                            if (state.isTotalPagesError)
                                Text(stringResource(R.string.number_not_valid))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Slider(
                    value = state.pagesReadValue.toFloat(),
                    onValueChange = {
                        state.changePagesReadValue(it.toInt())
                        onValuesChanged()
                    },
                    valueRange = 0f..state.totalPagesValue.toFloat(),
                    steps = max(0, state.totalPagesValue - 1),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
@Preview
private fun ProgressTabPreview() {
    MaterialTheme {
        Surface {
            ProgressTab(
                state = ProgressTabState()
            ) {}
        }
    }
}