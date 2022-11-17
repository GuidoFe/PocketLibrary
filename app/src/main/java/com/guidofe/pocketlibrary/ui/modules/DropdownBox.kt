package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                text()
            }
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
        }
    }
}