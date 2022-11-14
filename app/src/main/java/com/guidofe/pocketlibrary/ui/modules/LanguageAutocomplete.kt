package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.PopupProperties
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.utils.Constants

private data class Language(val code: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageAutocomplete(
    text: String,
    onTextChange: (String) -> Unit,
    label: @Composable () -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onDismissRequest: () -> Unit = {},
    isError: Boolean = false
) {
    val languageNames = stringArrayResource(R.array.language_names)
    val languageList = remember {
        Constants.languageCodes.mapIndexed { index, code ->
            Language(code, languageNames[index])
        }
    }
    var expanded by remember { mutableStateOf(false) }
    val filteredOptions = if (text.isBlank() || text.length < 2) emptyList() else languageList.filter {
        it.code.startsWith(text, true) || it.name.startsWith(text, true)
    }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                onTextChange(it)
                expanded = true
            },
            label = label,
            singleLine = true,
            enabled = enabled,
            isError = isError,
            supportingText = { if (isError) Text(stringResource(R.string.language_not_valid)) },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .onFocusChanged { state ->
                    expanded = state.isFocused
                }
                .fillMaxSize()
        )
        if (filteredOptions.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false; onDismissRequest(); },
                properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
            ) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text("${option.code} - ${option.name}") },
                        onClick = {
                            onOptionSelected(option.code)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}