package com.guidofe.pocketlibrary.utils

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri

fun Resources.getUri(id: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(this.getResourcePackageName(id))
        .appendPath(this.getResourceTypeName(id))
        .appendPath(this.getResourceEntryName(id))
        .build()
}