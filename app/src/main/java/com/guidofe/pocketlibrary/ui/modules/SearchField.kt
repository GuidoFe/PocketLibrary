package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
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
    onSearch: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var selection by remember { mutableStateOf(TextRange.Zero) }
    var composition: TextRange? by remember { mutableStateOf(null) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        selection = TextRange(value.length)
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = TextFieldValue(text = value, selection = selection, composition = composition),
        onValueChange = {
            onValueChange(it.text)
            selection = it.selection
            composition = it.composition
        },
        textStyle = LocalTextStyle.current,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            onSearch()
        }),
        modifier = modifier.focusRequester(focusRequester)
    )
}
@Composable
@Preview
private fun SearchFieldPreview() {
    PocketLibraryTheme() {
        SearchField(value = "Hello", onValueChange = {}) {
        }
    }
}