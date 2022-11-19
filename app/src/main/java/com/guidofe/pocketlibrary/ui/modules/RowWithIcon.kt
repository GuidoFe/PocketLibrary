package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thedeanda.lorem.Lorem
import com.thedeanda.lorem.LoremIpsum

@Composable
fun RowWithIcon(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    gap: Dp = 0.dp,
    boxPadding: Dp = 0.dp,
    boxWidth: Dp = 50.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val padding: Dp = 10.dp
    Box(
        modifier = Modifier
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
                .padding(0.dp, padding, padding, padding)
        ) {
            Box(
                modifier = Modifier
                    .width(boxWidth)
                    .padding(boxPadding),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(gap))
            content()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        )
    }
}

@Composable
@Preview
private fun RowWithIconPreview() {
    val lorem: Lorem = LoremIpsum.getInstance()
    MaterialTheme {
        Surface {
            RowWithIcon(
                icon = { Icon(Icons.Filled.Email, contentDescription = "Icon") },
                onClick = {}
            ) {
                Text(lorem.getTitle(3))
            }
        }
    }
}