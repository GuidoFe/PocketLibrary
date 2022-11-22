package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R

@Composable
fun EmptyBookCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(0.67f)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                5.dp,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.shapes.medium
            )
    ) {
        Text(
            stringResource(R.string.no_cover_sadface),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            softWrap = true,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}