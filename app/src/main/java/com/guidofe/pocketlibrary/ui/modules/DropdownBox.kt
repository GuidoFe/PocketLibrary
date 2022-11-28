package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.ui.utils.PreviewUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBox(
    text: @Composable () -> Unit,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .widthIn(min = 40.dp)
                ) {
                    text()
                }
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            }
        }
    }
}

@Composable
@Preview
fun DropdownBoxPreview() {
    PreviewUtils.ThemeColumn() {
        DropdownBox(text = { Text("Test") }, isExpanded = false)
    }
}