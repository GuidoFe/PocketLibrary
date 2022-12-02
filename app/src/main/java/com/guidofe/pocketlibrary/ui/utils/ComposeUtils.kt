package com.guidofe.pocketlibrary.ui.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBarColorAnimation(scrollBehavior: TopAppBarScrollBehavior?): State<Color> {
    return animateColorAsState(
        targetValue = lerp(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            FastOutLinearInEasing.transform(
                if ((scrollBehavior?.state?.overlappedFraction ?: 0f) >= 0.01f)
                    1f
                else
                    0f
            )
        ),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}