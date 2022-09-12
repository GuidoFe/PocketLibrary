package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.theme.PocketLibraryTheme

@Composable
fun BadgeIcon(painter: Painter,
              contentDescription: String,
              modifier: Modifier = Modifier,
              badgeBackground: Color = MaterialTheme.colors.primary,
              badgeForeground: Color = MaterialTheme.colors.onPrimary,
              badgeDiameter: Dp = 20.dp,
              iconProportion: Float = 0.75f,
) {
    Surface(shape = CircleShape,
            color = badgeBackground,
            modifier = modifier
                .size(badgeDiameter)
        ) {
       Icon(painter = painter,
           tint = badgeForeground,
           contentDescription = contentDescription,
           modifier = Modifier
               .padding((badgeDiameter - badgeDiameter * iconProportion) / 2)
           )
    }
}

@Preview
@Composable
fun BadgeIconPreview() {
    PocketLibraryTheme() {
        BadgeIcon(painterResource(R.drawable.ic_baseline_star_24), "Test", badgeBackground = Color.Red, badgeForeground = Color.White)
    }
}