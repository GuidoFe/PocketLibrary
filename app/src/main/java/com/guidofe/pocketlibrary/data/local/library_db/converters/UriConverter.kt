package com.guidofe.pocketlibrary.data.local.library_db.converters

import android.net.Uri
import androidx.room.TypeConverter

object UriConverter {
    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(uriString) }
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
}