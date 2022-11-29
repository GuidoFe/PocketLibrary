package com.guidofe.pocketlibrary.ui

import androidx.annotation.DrawableRes
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.ui.pages.destinations.BookLogPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.LandingPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.LibraryPageDestination
import com.guidofe.pocketlibrary.ui.pages.destinations.WishlistPageDestination
import com.ramcosta.composedestinations.spec.Direction

enum class BottomBarDestination(
    val direction: Direction,
    val labelId: Int,
    @DrawableRes val iconId: Int
) {
    LandingPageScreen(LandingPageDestination, R.string.home, R.drawable.home_24px),
    LibraryScreen(LibraryPageDestination(), R.string.my_library, R.drawable.collections_24px),
    WishlistScreen(WishlistPageDestination, R.string.wishlist, R.drawable.star_24px),
    BookLogScreen(BookLogPageDestination, R.string.book_log, R.drawable.book_hand_right_24px)
}