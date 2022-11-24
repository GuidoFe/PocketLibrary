package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalDividerWithText(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DividerDefaults.color,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Divider(modifier = Modifier.weight(1f), color = color)
        Box(
            modifier = if (onClick == null)
                Modifier.padding(5.dp)
            else
                Modifier.padding(5.dp).clickable {
                    onClick()
                }
        ) {
            label()
        }
        Divider(modifier = Modifier.weight(1f), color = color)
    }
}