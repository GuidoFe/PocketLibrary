package com.guidofe.pocketlibrary.data.local.library_db.converters

import android.net.Uri
import android.util.Log
import androidx.room.TypeConverter

object UriConverter {
    var baseUri: Uri = Uri.parse("")
    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let {
            val uri = Uri.parse(uriString)
            if (uri.isRelative) {
                val newUri = Uri.withAppendedPath(this.baseUri, uriString)
                Log.d("uri", "${uri.path} is relative, converted to ${newUri.path}")
                return newUri
            } else
                uri
        }
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
}