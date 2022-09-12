package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.guidofe.pocketlibrary.R

@Composable
fun DeletableChip(text: String, onDeleted: () -> Unit, modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.secondary) {
    androidx.compose.material.Surface(
        shape = RoundedCornerShape(400.dp),
        color = color,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(vertical = 5.dp)
                .padding(start = 10.dp, end = 5.dp)
        ) {
            Text(
                text,
                modifier = Modifier
                    .padding(end = 3.dp)
                )
            Surface(
                shape = CircleShape,
                color = Color(ColorUtils.blendARGB(color.toArgb(), Color.Black.toArgb(), 0.2f))
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_close_24),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = Modifier
                        .clickable {
                            onDeleted()
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewChip() {
    DeletableChip(text = "Frank Herbert", onDeleted = {})
}