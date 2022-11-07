package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedAutocomplete(
    text: String,
    onTextChange: (String) -> Unit,
    options: List<String>,
    label: @Composable () -> Unit,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true,
    onDismissRequest: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredOptions = if (text.isBlank()) options else options.filter {
        it.contains(text, true)
    }
    Box(modifier = Modifier.wrapContentSize()) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = label,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
            modifier = Modifier.onFocusChanged { state ->
                expanded = state.isFocused
            }
        )
        if (filteredOptions.isNotEmpty()) {
            DropdownMenu(
                expanded = options.isNotEmpty() && expanded,
                onDismissRequest = { expanded = false; onDismissRequest(); },
                properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
            ) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}