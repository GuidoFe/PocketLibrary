package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownBoxWithTrailingButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onIconClick: () -> Unit,
    buttonColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    BoxWithConstraints(
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = modifier
                    .fillMaxHeight()
                    .clip(
                        MaterialTheme.shapes.medium.copy(
                            topEnd = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    )
                    .background(textBackgroundColor)
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(min = 40.dp)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.contentColorFor(
                            textBackgroundColor
                        )
                    ) {
                        text()
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(
                        MaterialTheme.shapes.medium.copy(
                            topStart = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp)
                        )
                    )
                    .background(
                        if (enabled)
                            buttonColor
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    .clickable(enabled) { onIconClick() }
                    .padding(10.dp)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.contentColorFor(
                        if (enabled)
                            buttonColor
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                ) {
                    icon()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DropdownBoxWithTrailingButtonPreview() {
    Row {
        PocketLibraryTheme(darkTheme = false) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                DropdownBoxWithTrailingButton(
                    text = { Text("Test") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            "",
                        )
                    },
                    onIconClick = {},
                    buttonColor = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(10.dp))
                DropdownBoxWithTrailingButton(
                    text = { Text("Test") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            "",
                        )
                    },
                    onIconClick = {},
                    enabled = false,
                    buttonColor = MaterialTheme.colorScheme.primary
                )
            }
        }
        PocketLibraryTheme(darkTheme = true) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                DropdownBoxWithTrailingButton(
                    text = { Text("Test") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            "",
                        )
                    },
                    onIconClick = {},
                    buttonColor = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(10.dp))
                DropdownBoxWithTrailingButton(
                    text = { Text("Test") },
                    icon = {
                        Icon(
                            painterResource(R.drawable.archive_24px),
                            "",
                        )
                    },
                    onIconClick = {},
                    enabled = false,
                    buttonColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}