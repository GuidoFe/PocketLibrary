package com.guidofe.pocketlibrary.ui

import androidx.annotation.DrawableRes
import androidx.compose.material.ExperimentalMaterialApi
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.FavoritesPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.LandingPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.LibraryPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.StatsPageDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(val direction: DirectionDestinationSpec, val labelId: Int, @DrawableRes val iconId: Int) {
    @ExperimentalMaterialApi
    LandingPageScreen(LandingPageDestination, R.string.home, R.drawable.ic_baseline_home_24),
    LibraryScreen(LibraryPageDestination, R.string.my_library, R.drawable.ic_baseline_collections_24),
    FavoritesScreen(FavoritesPageDestination, R.string.favorites, R.drawable.ic_baseline_star_24),
    StatsScreen(StatsPageDestination, R.string.stats, R.drawable.ic_baseline_chart_outlined_24)
}