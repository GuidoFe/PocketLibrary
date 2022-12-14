package com.guidofe.pocketlibrary.ui.pages.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.model.AppStats
import com.guidofe.pocketlibrary.ui.modules.PieChart
import com.guidofe.pocketlibrary.ui.theme.CustomBlue
import com.guidofe.pocketlibrary.ui.theme.CustomGreen
import com.guidofe.pocketlibrary.ui.theme.CustomRed
import com.guidofe.pocketlibrary.ui.theme.CustomYellow

@Composable
internal fun StatsTile(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        content = content
    )
}

@Composable
internal fun LegendRow(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
internal fun NumberTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    StatsTile(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
internal fun PieStatsTile(
    stats: AppStats,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        StatsTile() {
            Column {
                Text(
                    stringResource(R.string.owned_books),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PieChart(
                    circleThickness = 40.dp,
                    innerCircleThickness = 40.dp,
                    circleColor = MaterialTheme.colorScheme.surface,
                    innerCircleColors = listOf(
                        CustomGreen,
                        CustomBlue,
                        CustomYellow,
                        CustomRed
                    ),
                    total = stats.libraryBooksCount.toFloat(),
                    values = listOf(
                        stats.libraryBooksRead.toFloat(),
                        stats.libraryBooksCurrentlyReading.toFloat(),
                        stats.libraryBooksSuspended.toFloat(),
                        stats.libraryBooksDnf.toFloat()
                    ),
                    cap = StrokeCap.Butt,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        LegendRow(
                            stringResource(R.string.read) +
                                ": ${stats.libraryBooksRead}",
                            color = CustomGreen
                        )
                        LegendRow(
                            stringResource(R.string.currently_reading) +
                                ": ${stats.libraryBooksCurrentlyReading}",
                            color = CustomBlue
                        )
                        LegendRow(
                            stringResource(R.string.suspended) +
                                ": ${stats.libraryBooksSuspended}",
                            color = CustomYellow
                        )
                        LegendRow(
                            stringResource(R.string.did_not_finish) +
                                ": ${stats.libraryBooksDnf}",
                            color = CustomRed
                        )
                        LegendRow(
                            stringResource(R.string.not_read) +
                                ": ${stats.libraryBooksNotRead}",
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}