package com.guidofe.pocketlibrary.ui.modules

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
              badgeBackground: Color = MaterialTheme.colorScheme.primary,
              badgeForeground: Color = MaterialTheme.colorScheme.onPrimary,
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
        BadgeIcon(painterResource(R.drawable.star_24px), "Test", badgeBackground = Color.Red, badgeForeground = Color.White)
    }
}