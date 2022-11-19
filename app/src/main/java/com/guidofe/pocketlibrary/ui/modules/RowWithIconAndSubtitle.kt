package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
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
fun RowWithIconAndSubtitle(
    icon: @Composable () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    gap: Dp = 0.dp,
    boxPadding: Dp = 0.dp,
    boxWidth: Dp = 50.dp,
    selectable: Boolean = false
) {
    val padding: Dp = 10.dp
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(0.dp, padding, padding, padding)
    ) {
        Box(
            modifier = Modifier
                .width(boxWidth)
                .fillMaxHeight()
                .padding(boxPadding),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(gap))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (selectable)
                SelectionContainer {
                    Text(
                        text,
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxLines = 1
                    )
                }
            else
                Text(
                    text,
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = 1
                )
        }
    }
}

@Composable
@Preview
private fun RowWithIconPreview() {
    val lorem: Lorem = LoremIpsum.getInstance()
    MaterialTheme {
        Surface {
            RowWithIconAndSubtitle(
                icon = { Icon(Icons.Filled.Email, contentDescription = "Icon") },
                title = lorem.getTitle(3),
                text = lorem.getParagraphs(2, 4)
            )
        }
    }
}