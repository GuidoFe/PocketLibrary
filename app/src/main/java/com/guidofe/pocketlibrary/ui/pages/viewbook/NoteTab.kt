package com.guidofe.pocketlibrary.ui.pages.viewbook

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.guidofe.pocketlibrary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTab(value: String, onValueChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var selection by remember { mutableStateOf(TextRange(value.length)) }
    var composition: TextRange? by remember { mutableStateOf(null) }
    var isFocused: Boolean by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        selection = TextRange(value.length)
        focusRequester.requestFocus()
    }
    Box(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (isFocused) {
                    focusManager.clearFocus()
                } else {
                    selection = TextRange(Int.MAX_VALUE)
                    focusRequester.requestFocus()
                }
            })
        }
    ) {
        BasicTextField(
            value = TextFieldValue(text = value, selection = selection, composition = composition),
            onValueChange = {
                onValueChange(it.text)
                selection = it.selection
                composition = it.composition
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Justify
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty() && !isFocused)
                        Text(
                            stringResource(R.string.note_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    innerTextField()
                }
            },
            modifier = Modifier.focusRequester(focusRequester).onFocusChanged {
                isFocused = it.isFocused
            }
        )
    }
}