package com.guidofe.pocketlibrary.utils

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import kotlin.math.pow
import kotlin.math.sqrt

fun Resources.getUri(id: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.getResourcePackageName(id))
        .appendPath(this.getResourceTypeName(id))
        .appendPath(this.getResourceEntryName(id))
        .build()
}

fun String?.nullIfEmptyOrBlank(): String? {
    return if (this.isNullOrBlank()) null else this
}

fun String.getInitials(): List<String> {
    return this.split(" ").filter { it.isNotBlank() && it.length > 1 }.map { it[0].uppercase() }
}

fun String.areInitialsEqual(b: String): Boolean {
    return this.getInitials() == b.getInitials()
}

@OptIn(ExperimentalPermissionsApi::class)
val PermissionStatus.isPermanentlyDenied: Boolean
    get() = !this.isGranted && !this.shouldShowRationale

/**
 * Convert Long into Int. If | value | is > Int.MAX, it will returned value % Int.MAX
 */
fun Long.toModInt(): Int {
    return (this % Int.MAX_VALUE).toInt()
}

fun IntOffset.distance(other: IntOffset): Double {
    val x = (this.x - other.x).toDouble().pow(2)
    val y = (this.y - other.y).toDouble().pow(2)
    return sqrt(x + y)
}