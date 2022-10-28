package com.guidofe.pocketlibrary.ui

import androidx.annotation.DrawableRes
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.destinations.FavoritesPageDestination
import com.guidofe.pocketlibrary.ui.destinations.LandingPageDestination
import com.guidofe.pocketlibrary.ui.destinations.LibraryPageDestination
import com.guidofe.pocketlibrary.ui.destinations.StatsPageDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(val direction: DirectionDestinationSpec, val labelId: Int, @DrawableRes val iconId: Int) {
    LandingPageScreen(LandingPageDestination, R.string.home, R.drawable.home_24px),
    LibraryScreen(LibraryPageDestination, R.string.my_library, R.drawable.collections_24px),
    FavoritesScreen(FavoritesPageDestination, R.string.favorites, R.drawable.star_24px),
    StatsScreen(StatsPageDestination, R.string.stats, R.drawable.chart_24px)
}