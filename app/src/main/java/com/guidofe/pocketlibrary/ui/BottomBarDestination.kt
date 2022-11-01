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
    WishlistScreen(WishlistPageDestination, R.string.wishlist, R.drawable.star_24px),
    BookLogScreen(BookLogPageDestination, R.string.book_log, R.drawable.borrow_book_24px)
}