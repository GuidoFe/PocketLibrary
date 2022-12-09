package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldRequestFocus: Boolean = false,
    onSearch: () -> Unit,
) {
    var focusRequester = remember { FocusRequester() }
    var selection by remember { mutableStateOf(TextRange.Zero) }
    var composition: TextRange? by remember { mutableStateOf(null) }
    BasicTextField(
        value = TextFieldValue(text = value, selection = selection, composition = composition),
        onValueChange = {
            onValueChange(it.text)
            selection = it.selection
            composition = it.composition
        },
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onValueChange(value.trim())
            onSearch()
        }),
        modifier = modifier.focusRequester(focusRequester)
    )

    LaunchedEffect(Unit) {
        if (shouldRequestFocus) {
            focusRequester.requestFocus()
        }
    }
}
@Composable
@Preview
private fun SearchFieldPreview() {
    PocketLibraryTheme() {
        SearchField(value = "Hello", onValueChange = {}) {
        }
    }
}