package com.guidofe.pocketlibrary.ui.modules

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.entities.ProgressPhase
import com.guidofe.pocketlibrary.ui.theme.ExtendedTheme
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme

private enum class CoverStatus { LOADED, LOADING, ERROR }

@Composable
fun SelectableBookCover(
    coverUri: Uri?,
    isSelected: Boolean,
    onTap: (Offset) -> Unit = {},
    onLongPress: (Offset) -> Unit = {},
    isLent: Boolean = false,
    progress: ProgressPhase? = null,
    colorFilter: ColorFilter? = null,
    enableDiskCache: Boolean = true
) {
    val shape = MaterialTheme.shapes.small
    val selectionOffset: Dp by animateDpAsState(
        if (isSelected) (-5).dp else 0.dp,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
    )
    var coverStatus: CoverStatus by remember { mutableStateOf(CoverStatus.LOADING) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .width(67.dp)
            .height(100.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = selectionOffset,
                    y = selectionOffset,
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 3.dp,
                        color = if (isLent)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                        shape = shape
                    )
            ) {
                if (coverUri == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                3.dp,
                                MaterialTheme.colorScheme.outline,
                                shape
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
                        model = if (enableDiskCache)
                            coverUri
                        else
                            ImageRequest.Builder(context).diskCachePolicy(CachePolicy.DISABLED)
                                .data(coverUri).build(),
                        contentDescription = stringResource(id = R.string.cover),
                        contentScale = ContentScale.Crop,
                        onLoading = { coverStatus = CoverStatus.LOADING },
                        onSuccess = { coverStatus = CoverStatus.LOADED },
                        onError = { coverStatus = CoverStatus.ERROR },
                        colorFilter = colorFilter,
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
                    CornerIcon(
                        cornerAlignment = Alignment.TopStart,
                        roundCornerSize = MaterialTheme.shapes.small.topStart,
                        background = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            painterResource(R.drawable.book_hand_right_24px),
                            stringResource(R.string.lent_adj),
                        )
                    }
            }
            progress?.let { p ->
                val background = when (p) {
                    ProgressPhase.READ -> ExtendedTheme.colors.green
                    ProgressPhase.SUSPENDED -> ExtendedTheme.colors.yellow
                    ProgressPhase.DNF -> ExtendedTheme.colors.red
                    else -> ExtendedTheme.colors.blue
                }
                CornerIcon(
                    cornerAlignment = Alignment.BottomStart,
                    roundCornerSize = MaterialTheme.shapes.small.bottomStart,
                    background = background,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = when (p) {
                            ProgressPhase.READ -> painterResource(R.drawable.done_24px)
                            ProgressPhase.IN_PROGRESS -> painterResource(
                                R.drawable.local_library_24px
                            )
                            ProgressPhase.SUSPENDED -> painterResource(R.drawable.pause_24px)
                            else -> painterResource(
                                R.drawable.do_not_disturb_on_24px
                            )
                        },
                        contentDescription = p.name,
                    )
                }
            }
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer
                                .copy(alpha = 0.6f)
                        )
                    // .background(Color.hsl(0f, 0f, 0f, 0.6f))
                ) {
                    Icon(
                        painterResource(R.drawable.check_24px),
                        stringResource(R.string.selected),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
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

@Preview
@Composable
private fun SelectableBookCoverPreview() {
    PocketLibraryTheme() {
        SelectableBookCover(
            coverUri = null,
            isSelected = false,
            isLent = true,
            progress = ProgressPhase.IN_PROGRESS
        )
    }
}