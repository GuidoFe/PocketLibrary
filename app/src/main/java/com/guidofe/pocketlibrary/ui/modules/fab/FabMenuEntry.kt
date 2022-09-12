package com.guidofe.pocketlibrary.ui.modules.fab

import androidx.compose.ui.graphics.painter.Painter

data class FabMenuEntry(val label: String, val icon: Painter, val onClick: () -> Unit = {})