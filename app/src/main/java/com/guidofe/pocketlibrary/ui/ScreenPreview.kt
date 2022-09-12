package com.guidofe.pocketlibrary.ui


sealed class ScreenPreview(pxWidth: Int, pxHeight: Int, density: Int) {
    val dpWidth = pxWidth * 160 / density
    val dpHeight = pxHeight * 160 / density
    object Samsung {
        object GalaxyS10: ScreenPreview(1080, 2400, 394)
        object GalaxyS8: ScreenPreview(2960, 1440, 570)
        object GalaxyNote10Plus: ScreenPreview(1080, 2280, 401)
        object GalaxyS7: ScreenPreview(1440, 2560, 577)
        object GalaxyA50: ScreenPreview(1080, 2340, 403)
    }
    object Xiaomi {
        object RedmiNote9Pro: ScreenPreview(1080, 2400, 395)
    }
    object LG {
        object Phoenix2: ScreenPreview(720, 1280, 294)
    }
}