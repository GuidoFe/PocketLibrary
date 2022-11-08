package com.guidofe.pocketlibrary.ui.modules

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.ui.theme.ExtendedTheme

private enum class CoverStatus { LOADED, LOADING, ERROR, EMPTY }

@Composable
fun SelectableBookCover(
    coverURI: Uri?,
    isSelected: Boolean,
    onTap: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    isLent: Boolean = false,
    progress: ProgressPhase? = null
) {
    val selectionOffset: Dp by animateDpAsState(
        if (isSelected) (-5).dp else 0.dp,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
    )
    var coverStatus: CoverStatus by remember { mutableStateOf(CoverStatus.LOADING) }
    Box(
        modifier = Modifier
            .width(67.dp)
            .height(100.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.shapes.medium
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = selectionOffset,
                    y = selectionOffset,
                )
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 3.dp,
                    color = if (isLent) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            if (coverURI == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            3.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.medium
                        )
                ) {
                    Text(
                        "NO COVER",
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        softWrap = true
                    )
                }
            } else {
                AsyncImage(
                    model = coverURI,
                    contentDescription = stringResource(id = R.string.cover),
                    contentScale = ContentScale.FillBounds,
                    onLoading = { coverStatus = CoverStatus.LOADING },
                    onSuccess = { coverStatus = CoverStatus.LOADED },
                    onError = { coverStatus = CoverStatus.ERROR },
                    modifier = Modifier
                        .fillMaxSize()
                )
                if (coverStatus == CoverStatus.LOADING) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (coverStatus == CoverStatus.ERROR) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            painterResource(R.drawable.error_24px),
                            stringResource(R.string.error)
                        )
                        Text(stringResource(R.string.error_sadface))
                    }
                }
            }
            if (isLent)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.lent), color = MaterialTheme.colorScheme.onPrimary)
                }
            progress?.let { p ->
                val background = when (p) {
                    ProgressPhase.READ -> ExtendedTheme.colors.green
                    ProgressPhase.SUSPENDED -> ExtendedTheme.colors.yellow
                    ProgressPhase.DNF -> ExtendedTheme.colors.red
                    ProgressPhase.IN_PROGRESS -> ExtendedTheme.colors.blue
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(25.dp).align(Alignment.TopEnd)
                        .clip(CircleShape).background(background).padding(3.dp)
                ) {
                    Icon(
                        painter = when (p) {
                            ProgressPhase.READ -> painterResource(R.drawable.done_24px)
                            ProgressPhase.IN_PROGRESS -> painterResource(
                                R.drawable.local_library_24px
                            )
                            ProgressPhase.SUSPENDED -> painterResource(R.drawable.pause_24px)
                            ProgressPhase.DNF -> painterResource(R.drawable.do_not_disturb_on_24px)
                        },
                        contentDescription = p.name,
                        tint = MaterialTheme.colorScheme.contentColorFor(background)
                    )
                }
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.hsl(0f, 0f, 0f, 0.6f))
                ) {
                    Icon(
                        painterResource(R.drawable.check_24px),
                        stringResource(R.string.selected),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .scale(2f)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = onLongPress,
                        onTap = onTap
                    )
                }
        )
    }
}