package com.guidofe.pocketlibrary.ui.utils

data class Menu<T>(
    val menuItems: Array<MenuItem<T>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Menu<*>

        if (!menuItems.contentEquals(other.menuItems)) return false

        return true
    }

    override fun hashCode(): Int {
        return menuItems.contentHashCode()
    }
}

data class MenuItem<T> (
    val labelId: (T) -> Int,
    val iconId: (T) -> Int,
    val onClick: (T) -> Unit,
    val isVisible: (T) -> Boolean = { true }
)