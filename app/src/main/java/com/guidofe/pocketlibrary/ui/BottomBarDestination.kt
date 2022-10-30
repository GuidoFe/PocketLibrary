package com.guidofe.pocketlibrary.ui

import androidx.annotation.DrawableRes
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.*
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val labelId: Int,
    @DrawableRes val iconId: Int
) {
    LandingPageScreen(LandingPageDestination, R.string.home, R.drawable.home_24px),
    LibraryScreen(LibraryPageDestination, R.string.my_library, R.drawable.collections_24px),
    // ICON <a href="https://www.flaticon.com/free-icons/wish-list" title="wish list icons">Wish list icons created by SBTS2018 - Flaticon</a>
    WishlistScreen(WishlistPageDestination, R.string.wishlist, R.drawable.star_24px),
    StatsScreen(StatsPageDestination, R.string.stats, R.drawable.chart_24px)
}